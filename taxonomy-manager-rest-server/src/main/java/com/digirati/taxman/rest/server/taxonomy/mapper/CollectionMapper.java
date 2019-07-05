package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.CollectionUriResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.Collection;
import java.util.Map;

public class CollectionMapper {

    private final ConceptIdResolver idResolver;

    private final CollectionUriResolver collectionUriResolver;

    private final RdfModelFactory factory;

    public CollectionMapper(ConceptIdResolver idResolver,
                            CollectionUriResolver collectionUriResolver,
                            RdfModelFactory factory) {
        this.idResolver = idResolver;
        this.collectionUriResolver = collectionUriResolver;
        this.factory = factory;
    }

    public CollectionModel map(Collection<ConceptRecord> concepts, String searchTerm) throws RdfModelException {
        var title = Map.of("en", "Search results for \"" + searchTerm + "\"");
        var builder = factory.createBuilder(CollectionModel.class)
                .setUri(collectionUriResolver.resolve())
                .addPlainLiteral(DCTerms.title, title);

        for (ConceptRecord concept : concepts) {
            builder.addEmbeddedModel(
                    SKOS.member,
                    factory.createBuilder(ConceptModel.class)
                            .addPlainLiteral(SKOS.prefLabel, concept.getPreferredLabel())
                            .setUri(idResolver.resolve(concept.getUuid())));
        }

        return builder.build();
    }
}
