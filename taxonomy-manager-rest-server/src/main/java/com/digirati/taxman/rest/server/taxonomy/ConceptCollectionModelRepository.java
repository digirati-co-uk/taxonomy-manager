package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import org.apache.jena.vocabulary.SKOS;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConceptCollectionModelRepository {

    @Inject
    ConceptDao conceptDao;

    @Inject
    ConceptMapper conceptMapper;

    @Inject
    RdfModelFactory modelFactory;

    /**
     * Find all relationships to the concept identified by {@code uuid} with a relationship of the given
     * {@code type}.
     *
     * @param uuid The identifier of the concept to find relationships for.
     * @param type The type of relationship to find.
     * @param depth The maximum depth of relationships to return.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public CollectionModel findRelated(UUID uuid, ConceptRelationshipType type, int depth) {
        try {
            var model = modelFactory.createBuilder(CollectionModel.class);
            model.setUri(URI.create("urn:generated"));

            conceptDao.findRelatedRecords(uuid, type)
                    .stream()
                    .map(record -> conceptMapper.map(new ConceptDataSet(record)))
                    .forEach(concept -> model.addEmbeddedModel(SKOS.member, concept));

            return model.build();
        } catch (RdfModelException ex) {
            throw new WebApplicationException("RDF graph for related concept is invalid", ex);
        }
    }
}
