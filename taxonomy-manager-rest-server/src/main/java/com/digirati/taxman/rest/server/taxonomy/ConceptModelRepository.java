package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxonomy.manager.storage.ConceptDao;
import com.digirati.taxonomy.manager.storage.ConceptDataSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.util.UUID;

/**
 * A repository that manages storage of {@link ConceptModel}s.
 */
@ApplicationScoped
public class ConceptModelRepository {

    @Inject
    ConceptMapper dataMapper;

    @Inject
    ConceptDao conceptDao;

    /**
     * Find an RDF model representation of a concept given an identifier.
     *
     * @param uuid An identifier of a Concept.
     * @return the RDF model of the concept requested.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ConceptModel find(UUID uuid) {
        ConceptDataSet dataset = conceptDao.loadDataSet(uuid);

        try {
            return dataMapper.map(dataset);
        } catch (RdfModelException e) {
            throw new WebApplicationException("Internal error occurred creating RDF model from dataset", e);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void update(ConceptModel model) {
        conceptDao.storeDataSet(dataMapper.map(model));
    }

    /**
     * Store a {@link ConceptModel} in the datastore and return the stored model, with URIs rewritten to
     * point to the taxonomy management API.
     *
     * @param model The model to be stored.
     * @return The updated model.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ConceptModel create(ConceptModel model) {
        var uuid = UUID.randomUUID();
        model.setUuid(uuid);

        var dataset = dataMapper.map(model);
        conceptDao.storeDataSet(dataset);

        return find(uuid);
    }
}
