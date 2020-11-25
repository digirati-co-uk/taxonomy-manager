package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.stream.Collectors;

/**
 * A POJO mapper that can convert between the representation of a {@code skos:ConceptScheme} in the database, and an RDF
 * object graph.
 */
public class ConceptSchemeMapper {

    private final ConceptIdResolver conceptIdResolver;
    private final ConceptSchemeIdResolver schemeIdResolver;
    private final RdfModelFactory modelFactory;
    private final ConceptMapper conceptMapper;

    public ConceptSchemeMapper(ConceptSchemeIdResolver schemeIdResolver, ConceptIdResolver conceptIdResolver,
                               RdfModelFactory modelFactory, ConceptMapper conceptMapper) {
        this.schemeIdResolver = schemeIdResolver;
        this.conceptIdResolver = conceptIdResolver;
        this.modelFactory = modelFactory;
        this.conceptMapper = conceptMapper;
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

        dataset.getTopConcepts()
                .stream()
                .map(conceptRecord -> conceptMapper.map(new ConceptDataSet(conceptRecord)))
                .forEach(conceptModel -> builder.addEmbeddedModel(SKOS.hasTopConcept, conceptModel));

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

        var record = new ConceptSchemeRecord(uuid, model.getProjectId());
        record.setTitle(model.getTitle());
        record.setSource(model.getSource());

        var topConcepts = model.getTopConcepts()
                .map(concept -> {
                    // Unsure if this is needed
                    var id = concept.getUuid();
                    if (id == null) {
                        concept.setUuid(conceptIdResolver.resolve(concept.getUri()).orElse(null));
                    }
                    return conceptMapper.map(concept).getRecord();
                })
                .collect(Collectors.toList());

        return new ConceptSchemeDataSet(record, topConcepts);
    }

}
