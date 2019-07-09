package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A POJO mapper that can convert between the representation of a {@code skos:ConceptScheme} in the database, and an RDF
 * object graph.
 */
public class ConceptSchemeMapper {

    private final ConceptIdResolver conceptIdResolver;
    private final ConceptSchemeIdResolver schemeIdResolver;
    private final RdfModelFactory modelFactory;

    public ConceptSchemeMapper(ConceptSchemeIdResolver schemeIdResolver, ConceptIdResolver conceptIdResolver, RdfModelFactory modelFactory) {
        this.schemeIdResolver = schemeIdResolver;
        this.conceptIdResolver = conceptIdResolver;
        this.modelFactory = modelFactory;
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

        for (ConceptReference topConceptReference : dataset.getTopConcepts()) {
            builder.addEmbeddedModel(
                    SKOS.hasTopConcept,
                    modelFactory.createBuilder(ConceptModel.class)
                            .addPlainLiteral(SKOS.prefLabel, topConceptReference.getPreferredLabel())
                            .setUri(conceptIdResolver.resolve(topConceptReference.getId())));
        }

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

        var topConcepts = model.getTopConcepts()
                .map(concept -> new ConceptReference(conceptIdResolver.resolve(concept.getUri()), Map.of()))
                .collect(Collectors.toList());

        return new ConceptSchemeDataSet(record, topConcepts);
    }


}