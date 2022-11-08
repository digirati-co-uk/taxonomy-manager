package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxman.rest.server.infrastructure.config.RdfConfig;
import com.digirati.taxman.rest.server.infrastructure.event.ConceptEvent;
import com.digirati.taxman.rest.server.infrastructure.event.ConceptEventListener;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.SearchResultsMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRelationshipRecord;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.MultimapBuilder;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A repository that manages storage of {@link ConceptModel}s.
 */
@ApplicationScoped
public class ConceptModelRepository {

    /**
     * This is currently set to false, as we don't fully support transitive relationships.
     * It can be modified and tested in the future releases.
     */
    private static final boolean USE_TRANSITIVE_RELATIONSHIPS = false;

    @Inject
    ConceptMapper conceptMapper;

    @Inject
    SearchResultsMapper searchResultsMapper;

    @Inject
    ConceptDao conceptDao;

    @Inject
    ConceptEventListener eventPublisher;

    @Inject
    ConceptIdResolver idResolver;

    /**
     * Find an RDF model representation of a concept given an identifier.
     *
     * @param uuid An identifier of a Concept.
     * @return the RDF model of the concept requested.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<ConceptModel> find(UUID uuid) {
        var dataset = conceptDao.loadDataSet(uuid);

        try {
            RdfConfig.isInGet.set(true);

            return dataset.isEmpty() ? Optional.empty() : Optional.of(conceptMapper.map(dataset.get())).map(model -> {
                var props = CRU_STMTS.computeIfAbsent(uuid, (k) -> new HashMap<>());
                for (var prop : CRU_PROPS) {
                    if (!props.containsKey(prop)) {
                        continue;
                    }

                    var stmt = props.get(prop);
                    Resource resource = model.getResource();
                    resource.removeAll(prop);

                    if (stmt != null) {
                        resource.addProperty(stmt.getPredicate().inModel(resource.getModel()), stmt.getObject().inModel(resource.getModel()));
                    }
                }

                return model;
            });
        } finally {
            RdfConfig.isInGet.set(false);
        }
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

    private static List<Property> CRU_PROPS = List.of(
            RdfConfig.inCommodityGroup,
            RdfConfig.inRegionGroup,
            RdfConfig.inTopicGroup,
            RdfConfig.isTopicGroup,
            RdfConfig.isCommodityGroup,
            RdfConfig.isRegionGroup
    );

    public static final Map<UUID, Map<Property, Statement>> CRU_STMTS = new ConcurrentHashMap<>();

    /**
     * Perform an idempotent update of an existing {@link ConceptModel}, updating all stored properties
     * as well relationships.
     */
    public void update(ConceptModel model) {
        ConceptModel existing = null;
        if (model.getUuid() == null) {
            model.setUuid(UUID.randomUUID());
        } else {
            var dataSet = conceptDao.loadDataSet(model.getUuid());
            if (dataSet.isPresent()) {
                existing = conceptMapper.map(dataSet.get());
            }
        }


        var resource = model.getResource();
        var props = CRU_STMTS.computeIfAbsent(model.getUuid(), k -> new HashMap<>());
        for (var prop : CRU_PROPS) {
            var stmt = resource.getProperty(prop);
            props.put(prop, stmt);
        }

        conceptDao.storeDataSet(conceptMapper.map(model));
        applySymmetricRelationChanges(model, existing);

        eventPublisher.notify(ConceptEvent.updated(model, existing));
    }

    /**
     * Gets a stream of UUIDs of all the concept in the specified relationship to the provided model.
     * Can be empty, not null.
     *
     * @param conceptModel            The model to load relationships for
     * @param conceptRelationshipType Type of the relationship to consider
     * @return Stream of UUIDs of concepts in the specified relationship to the provided model
     */
    private Stream<UUID> getRelationshipsOrEmpty(ConceptModel conceptModel,
                                                 ConceptRelationshipType conceptRelationshipType) {
        return conceptModel
                .getRelationships(conceptRelationshipType, USE_TRANSITIVE_RELATIONSHIPS)
                .map(relatedConceptModel -> idResolver.resolve(relatedConceptModel.getUri()))
                .flatMap(Optional::stream)
                .distinct();
    }

    /**
     * For the symmetric relationship creation, we only want to apply it, if a new relationship has been
     * created. We do not get that information directly, but we can compare the related Concepts that currently
     * exist in the persistence to the incoming version of the object, obtaining only the new ones.
     *
     * @param newModel                The incoming model to update
     * @param conceptRelationshipType The type of the relationship to work with
     * @param existingModel           (Can be null) The existing version of the model to update
     * @return Stream of UUIDs of concepts in the specified relationship type, that have not existed before update
     */
    private Stream<UUID> getNewRelationships(ConceptModel newModel, ConceptRelationshipType conceptRelationshipType,
                                             ConceptModel existingModel) {
        if (existingModel == null) {
            return getRelationshipsOrEmpty(newModel, conceptRelationshipType);
        }

        Set<UUID> existing = getRelationshipsOrEmpty(existingModel, conceptRelationshipType)
                .collect(Collectors.toSet());
        return getRelationshipsOrEmpty(newModel, conceptRelationshipType)
                .filter(c -> !existing.contains(c));
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

        var resource = model.getResource();
        var props = CRU_STMTS.computeIfAbsent(uuid, k -> new HashMap<>());
        for (var prop : CRU_PROPS) {
            var stmt = resource.getProperty(prop);
            props.put(prop, stmt);
        }

        var dataset = conceptMapper.map(model);
        conceptDao.storeDataSet(dataset);
        eventPublisher.notify(ConceptEvent.created(model));

        return find(uuid).orElseThrow();
    }

    /**
     * Analyze the changes between relationships and create symmetric relations if needed.
     *
     * @param model    The new data for the Concept
     * @param existing (Can be null) The current version of the Concept, if one exists
     */
    public void applySymmetricRelationChanges(ConceptModel model, ConceptModel existing) {

        BiConsumer<UUID, ConceptRelationshipType> createRelationshipToModel = (relatedUuid, relationshipType) -> {
            var conceptDataSetOptional = conceptDao.loadDataSet(relatedUuid);
            if (conceptDataSetOptional.isEmpty()) {
                // Can't do anything for a non-existent item
                return;
            }
            ConceptDataSet conceptDataSet = conceptDataSetOptional.get();
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
            eventPublisher.notify(ConceptEvent.updated(conceptMapper.map(conceptDataSet), existing));
        };

        // For each broader, create a narrower relationship to this
        // For each narrower, create a broader relationship to this
        for (var type : Set.of(ConceptRelationshipType.BROADER, ConceptRelationshipType.NARROWER)) {
            getNewRelationships(model, type, existing)
                    .forEach(relatedUuid
                            -> createRelationshipToModel.accept(relatedUuid, type.inverse()));
        }
    }

    /**
     * Deletes the concept.
     *
     * @param uuid identifier of the concept
     */
    public void delete(UUID uuid) {
        var concept = find(uuid);
        if (concept.isEmpty()) {
            // Perfect.
            return;
        }

        conceptDao.deleteDataSet(uuid);

        eventPublisher.notify(ConceptEvent.deleted(concept.get()));
    }
}
