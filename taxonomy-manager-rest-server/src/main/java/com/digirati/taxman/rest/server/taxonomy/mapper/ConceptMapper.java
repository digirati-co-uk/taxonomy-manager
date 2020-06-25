package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelBuilder;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ProjectIdResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRelationshipRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * A POJO mapper that can map between {@link ConceptModel}s and {@link ConceptDataSet}s.
 */
public class ConceptMapper {

    private final ConceptIdResolver conceptIdResolver;
    private final ProjectIdResolver projectIdResolver;
    private final RdfModelFactory factory;

    /**
     * Creates new instance of the ConceptMapper.
     * @param conceptIdResolver Resolver from Concept Id to Concept URI
     * @param projectIdResolver Resolver from Project Slug to Project URI
     * @param modelFactory General factory of all RDF models
     */
    public ConceptMapper(
            ConceptIdResolver conceptIdResolver,
            ProjectIdResolver projectIdResolver,
            RdfModelFactory modelFactory) {
        this.conceptIdResolver = conceptIdResolver;
        this.projectIdResolver = projectIdResolver;
        this.factory = modelFactory;
    }

    /**
     * Convert a database data representation to a typed RDF model.
     *
     * @param dataset The database dataset to convert.
     * @return a typed RDF model.
     */
    public ConceptModel map(ConceptDataSet dataset) {
        try {
            var builder = factory.createBuilder(ConceptModel.class);
            var record = dataset.getRecord();
            var extractor = new ConceptLabelExtractor(record);

            builder.setUri(conceptIdResolver.resolve(record.getUuid()));
            extractor.extractTo(builder);

            if (StringUtils.isNotBlank(record.getSource())) {
                builder.addEmbeddedModel(DCTerms.source, URI.create(record.getSource()));
            }

            if (StringUtils.isNotBlank(record.getProjectSlug())) {
                builder.addEmbeddedModel(DCTerms.isPartOf, factory.createBuilder(ProjectModel.class)
                    .setUri(projectIdResolver.resolve(record.getProjectSlug()))
                );
            }

            for (ConceptRelationshipRecord relationship : dataset.getRelationshipRecords()) {
                var type = relationship.getType();
                var property = type.getSkosProperty(relationship.isTransitive());
                var source = relationship.getTargetSource();

                RdfModelBuilder<ConceptModel> embeddedModel = factory.createBuilder(ConceptModel.class)
                        .addPlainLiteral(SKOS.prefLabel, relationship.getTargetPreferredLabel())
                        .setUri(conceptIdResolver.resolve(relationship.getTarget()));

                if (source != null) {
                    embeddedModel.addEmbeddedModel(DCTerms.source, URI.create(source));
                }

                builder.addEmbeddedModel(property, embeddedModel);
            }

            ConceptModel concept = builder.build();
            concept.setUuid(dataset.getRecord().getUuid());
            return concept;
        } catch (RdfModelException ex) {
            throw new WebApplicationException("Mapping concept from data records produced invalid RDF", ex);
        }
    }

    /**
     * Convert a typed RDF model to a database data representation.
     *
     * @param model The typed RDF model to convert.
     * @return a database data representation of the {@link ConceptModel}.
     */
    public ConceptDataSet map(ConceptModel model) {
        final UUID uuid = model.getUuid();

        var record = new ConceptRecord(uuid);
        record.setSource(model.getSource());
        record.setPreferredLabel(model.getPreferredLabel());
        record.setAltLabel(model.getAltLabel());
        record.setHiddenLabel(model.getHiddenLabel());
        record.setNote(model.getNote());
        record.setChangeNote(model.getChangeNote());
        record.setEditorialNote(model.getEditorialNote());
        record.setExample(model.getExample());
        record.setHistoryNote(model.getHistoryNote());
        record.setScopeNote(model.getScopeNote());

        var relationshipRecords = new ArrayList<ConceptRelationshipRecord>();

        for (var type : ConceptRelationshipType.VALUES) {
            boolean transitiveSupported = type.hasTransitiveProperty();

            Stream<ConceptModel> relationships = model.getRelationships(type, false);
            Stream<ConceptModel> transitiveRelationships = transitiveSupported
                    ? model.getRelationships(type, true)
                    : Stream.of();

            BiConsumer<ConceptModel, Boolean> relationshipMapper = (resource, transitive) -> {
                var targetUri = resource.getUri();
                var targetUuid = conceptIdResolver.resolve(targetUri).orElse(UUID.randomUUID());
                var targetSource = resource.getSource();

                var relationshipRecord =
                        new ConceptRelationshipRecord(uuid, targetUuid, targetSource, type, transitive);
                relationshipRecords.add(relationshipRecord);
            };

            relationships.forEach(r -> relationshipMapper.accept(r, false));
            transitiveRelationships.forEach(tr -> relationshipMapper.accept(tr, true));
        }

        return new ConceptDataSet(record, relationshipRecords);
    }

}
