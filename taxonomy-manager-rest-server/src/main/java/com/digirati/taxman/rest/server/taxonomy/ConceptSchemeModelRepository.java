package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptSchemeMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDataSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCTerms;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.util.UUID;

@ApplicationScoped
public class ConceptSchemeModelRepository {

    @Inject
    ConceptSchemeDao conceptSchemeDao;

    @Inject
    ConceptSchemeMapper dataMapper;

    /**
     * Find an RDF model representation of a concept scheme given an identifier.
     *
     * @param uuid An identifier of a ConceptScheme.
     * @return the RDF model of the concept requested.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ConceptSchemeModel find(UUID uuid) {
        ConceptSchemeDataSet dataset = conceptSchemeDao.loadDataSet(uuid);

        try {
            return dataMapper.map(dataset);
        } catch (RdfModelException e) {
            throw new WebApplicationException("Internal error occurred creating RDF model from dataset", e);
        }
    }

    /**
     * Store an RDF model of a {@code skos:ConceptScheme} in the database as a new record, along with any
     * top-level relationships, returning the updated model from the database..
     *
     * @param model The {@code skos:ConceptScheme} model to store.
     * @return the stored {@link com.digirati.taxman.common.taxonomy.ConceptSchemeModel}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ConceptSchemeModel create(ConceptSchemeModel model) {
        String originalUri = model.getResource().getURI();
        if (StringUtils.isNotBlank(originalUri)) {
            model.getResource().addProperty(DCTerms.source, originalUri);
        }
        var uuid = UUID.randomUUID();
        model.setUuid(uuid);

        var dataset = dataMapper.map(model);
        conceptSchemeDao.storeDataSet(dataset);

        return find(uuid);
    }

    /**
     * Perform an idempotent update of an existing {@link ConceptSchemeModel}, updating all stored properties
     * as well as top concept relationships.
     *
     * @return {@code true} iff the operation updated any records, {@code false} if no change occurred.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean update(ConceptSchemeModel conceptScheme) {
        conceptSchemeDao.loadDataSet(conceptScheme.getUuid());
        ConceptSchemeDataSet dataset = dataMapper.map(conceptScheme);

        return conceptSchemeDao.storeDataSet(dataset);
    }
}
