package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRelationshipRecord;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import java.net.URI;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * A POJO mapper that can map between {@link ConceptModel}s and {@link ConceptDataSet}s.
 */
public class ConceptMapper {

    private final ConceptIdResolver idResolver;
    private final RdfModelFactory factory;

    public ConceptMapper(ConceptIdResolver idResolver, RdfModelFactory modelFactory) {
        this.idResolver = idResolver;
        this.factory = modelFactory;
    }

    /**
     * Convert a database data representation to a typed RDF model.
     *
     * @param dataset The database dataset to convert.
     * @return a typed RDF model.
     * @throws RdfModelException if the RDF produced from the dataset was invalid.
     */
    public ConceptModel map(ConceptDataSet dataset) throws RdfModelException {
        var builder = factory.createBuilder(ConceptModel.class);
        var record = dataset.getRecord();

        builder.setUri(idResolver.resolve(record.getUuid()));
        builder.addPlainLiteral(SKOS.prefLabel, record.getPreferredLabel())
                .addPlainLiteral(SKOS.altLabel, record.getAltLabel())
                .addPlainLiteral(SKOS.hiddenLabel, record.getHiddenLabel())
                .addPlainLiteral(SKOS.note, record.getNote())
                .addPlainLiteral(SKOS.changeNote, record.getChangeNote())
                .addPlainLiteral(SKOS.editorialNote, record.getEditorialNote())
                .addPlainLiteral(SKOS.example, record.getExample())
                .addPlainLiteral(SKOS.historyNote, record.getHistoryNote())
                .addPlainLiteral(SKOS.scopeNote, record.getScopeNote());

        for (ConceptRelationshipRecord relationship : dataset.getRelationshipRecords()) {
            var type = relationship.getType();
            var property = type.getSkosProperty(relationship.isTransitive());

            builder.addEmbeddedModel(
                    property,
                    factory.createBuilder(ConceptModel.class)
                            .addPlainLiteral(SKOS.prefLabel, relationship.getTargetPreferredLabel())
                            .setUri(idResolver.resolve(relationship.getTarget())));
        }

        ConceptModel concept = builder.build();
        concept.setUuid(dataset.getRecord().getUuid());
        return concept;
    }

    /**
     * Convert a typed RDF model to a database data representation.
     *
     * @param model The typed RDF model to convert.
     * @return a database data representation of the {@link ConceptModel}.
     */
    public ConceptDataSet map(ConceptModel model) {
        var uuid = model.getUuid();

        var record = new ConceptRecord(uuid);
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

            Stream<Resource> relationships = model.getRelationships(type, false);
            Stream<Resource> transitiveRelationships = transitiveSupported ? model.getRelationships(type, true) : Stream.of();

            BiConsumer<Resource, Boolean> relationshipMapper = (resource, transitive) -> {
                var targetUri = resource.getURI();
                var targetUuid = idResolver.resolve(URI.create(targetUri));
                var relationshipRecord = new ConceptRelationshipRecord(uuid, targetUuid, type, transitive);

                relationshipRecords.add(relationshipRecord);
            };

            relationships.forEach(r -> relationshipMapper.accept(r, false));
            transitiveRelationships.forEach(tr -> relationshipMapper.accept(tr, true));
        }

        return new ConceptDataSet(record, relationshipRecords);
    }
}
