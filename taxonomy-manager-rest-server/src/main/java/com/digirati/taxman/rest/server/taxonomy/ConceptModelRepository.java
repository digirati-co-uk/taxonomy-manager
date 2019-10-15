package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.rest.server.infrastructure.event.ConceptEvent;
import com.digirati.taxman.rest.server.infrastructure.event.EventService;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.SearchResultsMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRelationshipRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.vocabulary.DCTerms;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A repository that manages storage of {@link ConceptModel}s.
 */
@ApplicationScoped
public class ConceptModelRepository {

    @Inject
    ConceptMapper conceptMapper;

    @Inject
    SearchResultsMapper searchResultsMapper;

    @Inject
    ConceptDao conceptDao;

    @Inject
    EventService eventService;

    @Inject
    ConceptIdResolver idResolver;


    /**
     * Find an RDF model representation of a concept given an identifier.
     *
     * @param uuid An identifier of a Concept.
     * @return the RDF model of the concept requested.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ConceptModel find(UUID uuid) {
        ConceptDataSet dataset = conceptDao.loadDataSet(uuid);

        return conceptMapper.map(dataset);
    }

    /**
     * Find all concepts with a preferred label in any language beginning with a given String.
     *
     * @param partialLabel the label substring to search for
     * @return all concepts with preferred labels beginning with the given substring
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public CollectionModel findByPartialLabel(String partialLabel, String languageKey) {
        Collection<ConceptRecord> concepts = conceptDao.getConceptsByPartialLabel(partialLabel, languageKey);
        return searchResultsMapper.map(concepts, partialLabel);
    }

    /**
     * Find an RDF model representation of a concept given an identifier.
     *
     * @param uuid An identifier of a Concept.
     * @return the RDF model of the concept requested.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<ConceptModel> findAll(Collection<UUID> uuid) {
        return conceptDao.findAllRecords(uuid)
                .stream()
                .map(record -> conceptMapper.map(new ConceptDataSet(record)))
                .collect(Collectors.toList());
    }

    /**
     * Perform an idempotent update of an existing {@link ConceptModel}, updating all stored properties
     * as well relationships.
     */
    public void update(ConceptModel model) {
        if (model.getUuid() == null) {
            model.setUuid(UUID.randomUUID());
        }

        applySymmetricRelationChanges(model);

        conceptDao.storeDataSet(conceptMapper.map(model));
        eventService.send(ConceptEvent.updated(model));
    }

    private static final boolean USE_TRANSITIVE_RELATIONSHIPS = false;

    private Stream<UUID> getRelationshipsOrEmpty(ConceptModel conceptModel, ConceptRelationshipType conceptRelationshipType) {
        return conceptModel
                .getRelationships(conceptRelationshipType, USE_TRANSITIVE_RELATIONSHIPS)
                .map(relatedConceptModel -> idResolver.resolve(relatedConceptModel.getUri()))
                .flatMap(Optional::stream)
                .distinct();
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
        String originalUri = model.getResource().getURI();
        if (StringUtils.isNotBlank(originalUri)) {
            model.getResource().addProperty(DCTerms.source, originalUri);
        }

        if (model.isNew()) {
            model.setUuid(UUID.randomUUID());
        }

        var uuid = model.getUuid();
        var dataset = conceptMapper.map(model);
        conceptDao.storeDataSet(dataset);
        eventService.send(ConceptEvent.created(model));

        return find(uuid);
    }

    /**
     * Analyze the changes between relationships and create symmetric relations if needed
     * @param model new model
     */
    private void applySymmetricRelationChanges(ConceptModel model) {



        BiConsumer<UUID,ConceptRelationshipType> createRelationshipToModel = (relatedUuid, relationshipType)->
        {
            ConceptDataSet conceptDataSet = conceptDao.loadDataSet(relatedUuid);
            conceptDataSet.addRelationshipRecord(
                    new ConceptRelationshipRecord(
                            relatedUuid,
                            model.getUuid(),
                            model.getSource(),
                            relationshipType,
                            USE_TRANSITIVE_RELATIONSHIPS
                    )
            );

            conceptDao.storeDataSet(conceptDataSet);
            eventService.send(ConceptEvent.updated(conceptMapper.map(conceptDataSet)));
        };

        // For each broader, create a narrower relationship to this
        // For each narrower, create a broader relationship to this
        for (var type : Set.of(ConceptRelationshipType.BROADER, ConceptRelationshipType.NARROWER)) {
            getRelationshipsOrEmpty(model, type)
                    .forEach(relatedUuid
                            -> createRelationshipToModel.accept(relatedUuid, type.inverse()));
        }
    }
}
