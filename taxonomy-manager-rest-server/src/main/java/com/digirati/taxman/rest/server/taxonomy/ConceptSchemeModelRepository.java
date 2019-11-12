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
import java.util.Optional;
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
    public Optional<ConceptSchemeModel> find(UUID uuid) {
        var dataset = conceptSchemeDao.loadDataSet(uuid);
        if (dataset.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(dataMapper.map(dataset.get()));
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
    public ConceptSchemeModel create(ConceptSchemeModel model) {
        // If the Concept comes with an existing URI, then store it as a dcterms:source
        String originalUri = model.getResource().getURI();
        if (StringUtils.isNotBlank(originalUri)) {
            model.getResource().addProperty(DCTerms.source, originalUri);
        }

        // If the model comes without an UUID, create a new one
        if (model.getUuid() == null) {
            model.setUuid(UUID.randomUUID());
        }

        var uuid = update(model);

        return find(uuid).orElseThrow();
    }

    /**
     * Perform an idempotent update of an existing {@link ConceptSchemeModel}, updating all stored properties
     * as well as top concept relationships.
     *
     * @return UUID of the record updated
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public UUID update(ConceptSchemeModel conceptScheme) {
        ConceptSchemeDataSet dataset = dataMapper.map(conceptScheme);

        return conceptSchemeDao.storeDataSet(dataset);
    }

    public void delete(UUID uuid) {
        conceptSchemeDao.deleteDataSet(uuid);
    }
}
