package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.taxonomy.Concept;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeImportModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.infrastructure.event.ConceptEvent;
import com.digirati.taxman.rest.server.infrastructure.event.EventService;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptSchemeMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDataSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCTerms;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConceptSchemeModelRepository {

    @Inject
    ConceptSchemeDao conceptSchemeDao;

    @Inject
    ConceptDao conceptDao;

    @Inject
    ConceptSchemeMapper conceptSchemeMapper;

    @Inject
    ConceptMapper conceptMapper;

    @Inject
    ConceptIdResolver conceptIdResolver;

    @Inject
    EventService eventService;

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
            return conceptSchemeMapper.map(dataset);
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

        var dataset = conceptSchemeMapper.map(model);
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
        ConceptSchemeDataSet dataset = conceptSchemeMapper.map(conceptScheme);

        return conceptSchemeDao.storeDataSet(dataset);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public ConceptSchemeModel importScheme(ConceptSchemeImportModel importModel) {
        ConceptSchemeModel conceptScheme = importModel.getConceptScheme();
        Collection<ConceptModel> conceptsToImport = importModel.getConcepts();

        Map<URI, UUID> originalUriToGeneratedUri = new HashMap<>();
        for (ConceptModel concept : conceptsToImport) {
            URI originalUri = concept.getUri();
            UUID generatedUuid = UUID.randomUUID();
            originalUriToGeneratedUri.put(originalUri, generatedUuid);
        }

        List<Concept> importedConcepts = conceptMapper.map(conceptsToImport, originalUriToGeneratedUri).stream()
                .map(conceptDataSet -> {
                    conceptDao.storeDataSet(conceptDataSet);
                    return conceptMapper.map(conceptDataSet);
                }).collect(Collectors.toList());

        List<ConceptModel> topConceptModels = conceptScheme.getTopConcepts().collect(Collectors.toList());
        List<Concept> topConcepts = conceptMapper.map(topConceptModels, originalUriToGeneratedUri)
                .stream()
                .map(ConceptDataSet::getRecord)
                .collect(Collectors.toList());

        UUID conceptSchemeUuid = UUID.randomUUID();
        conceptScheme.setUuid(conceptSchemeUuid);
        ConceptSchemeDataSet schemeDataSet = conceptSchemeMapper.map(conceptScheme, topConcepts);
        conceptSchemeDao.storeDataSet(schemeDataSet);

        Set<UUID> importedConceptUuids = importedConcepts.stream()
                .map(Concept::getUuid)
                .collect(Collectors.toSet());

        List<ConceptModel> imported = conceptDao.findAllRecords(importedConceptUuids).stream()
                .map(record -> conceptMapper.map(new ConceptDataSet(record)))
                .collect(Collectors.toList());
        eventService.send(ConceptEvent.importConcepts(imported));

        return find(conceptSchemeUuid);
    }
}
