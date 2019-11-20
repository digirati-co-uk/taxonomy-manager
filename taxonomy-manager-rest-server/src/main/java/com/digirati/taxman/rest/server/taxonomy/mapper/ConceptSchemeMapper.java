package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ProjectIdResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A POJO mapper that can convert between the representation of a {@code skos:ConceptScheme} in the database, and an RDF
 * object graph.
 */
public class ConceptSchemeMapper {

    private final ConceptIdResolver conceptIdResolver;
    private final ConceptSchemeIdResolver schemeIdResolver;
    private final RdfModelFactory modelFactory;
    private final ProjectIdResolver projectIdResolver;

    public ConceptSchemeMapper(ConceptSchemeIdResolver schemeIdResolver, ConceptIdResolver conceptIdResolver, RdfModelFactory modelFactory, ProjectIdResolver projectIdResolver) {
        this.schemeIdResolver = schemeIdResolver;
        this.conceptIdResolver = conceptIdResolver;
        this.modelFactory = modelFactory;
        this.projectIdResolver = projectIdResolver;
    }

    /**
     * Convert a database data representation to a typed RDF model.
     *
     * @param dataset The database dataset to map.
     * @return A RDF representation of the provided database records.
     * @throws RdfModelException if an error occurred building an RDF model.
     */
    public ConceptSchemeModel map(ConceptSchemeDataSet dataset) throws RdfModelException {
        var builder = modelFactory.createBuilder(ConceptSchemeModel.class);
        var record = dataset.getRecord();

        builder.setUri(schemeIdResolver.resolve(record.getUuid()));
        builder.addPlainLiteral(DCTerms.title, record.getTitle());

        if (StringUtils.isNotBlank(record.getSource())) {
            builder.addStringProperty(DCTerms.source, record.getSource());
        }

        for (ConceptReference topConceptReference : dataset.getTopConcepts()) {
            var embeddedModel = modelFactory.createBuilder(ConceptModel.class)
                    .addPlainLiteral(SKOS.prefLabel, topConceptReference.getPreferredLabel())
                    .setUri(conceptIdResolver.resolve(topConceptReference.getId()));

            var source = topConceptReference.getSource();
            source.ifPresent(uri -> embeddedModel.addEmbeddedModel(DCTerms.source, URI.create(uri)));

            builder.addEmbeddedModel(SKOS.hasTopConcept, embeddedModel);
        }

        builder.addEmbeddedModel(DCTerms.isPartOf, projectIdResolver.resolve(dataset.getOwnerSlug()));

        return builder.build();
    }

    /**
     * Convert a typed RDF model to database data representation.
     *
     * @param model The typed RDF model to map.
     * @return A {@link ConceptSchemeDataSet} representing records to be passed to the database.
     */
    public ConceptSchemeDataSet map(ConceptSchemeModel model) {
        var uuid = model.getUuid();

        var record = new ConceptSchemeRecord(uuid);
        record.setTitle(model.getTitle());
        record.setSource(model.getSource());

        var topConcepts = model.getTopConcepts()
                .map(concept -> {
                    UUID id = concept.getUuid();
                    Resource resource = concept.getResource();

                    if (id == null) {
                        id = conceptIdResolver.resolve(concept.getUri()).map(UUID::fromString).orElse(null);
                    }

                    var targetSourceResource = resource.getPropertyResourceValue(DCTerms.source);

                    String targetSourceResourceUri = null;
                    if (targetSourceResource != null) {
                        targetSourceResourceUri = targetSourceResource.getURI();
                    }

                    return new ConceptReference(id, targetSourceResourceUri, ArrayListMultimap.create());
                })
                .collect(Collectors.toList());

        var ownerSlug = model.getProject().getSlug();

        return new ConceptSchemeDataSet(record, topConcepts, ownerSlug);
    }

}
