package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service which encapsulates the business logic needed to read SKOS from and write SKOS to the
 * database.
 */
public class SkosPersistenceService {

    private static final Logger logger = LogManager.getLogger(SkosPersistenceService.class);

    private final ConnectionProvider connectionProvider;

    private final ConceptDao conceptDao;

    private final ConceptSchemeDao conceptSchemeDao;

    private final RelationshipDao relationshipDao;

    private final SkosTranslator skosTranslator;

    public SkosPersistenceService() {
        this.connectionProvider = new ConnectionProvider();
        this.conceptDao = new ConceptDao();
        this.conceptSchemeDao = new ConceptSchemeDao();
        this.relationshipDao = new RelationshipDao();
        this.skosTranslator = new SkosTranslator();
    }

    SkosPersistenceService(
            ConnectionProvider connectionProvider,
            ConceptDao conceptDao,
            ConceptSchemeDao conceptSchemeDao,
            RelationshipDao relationshipDao,
            SkosTranslator skosTranslator) {
        this.connectionProvider = connectionProvider;
        this.conceptDao = conceptDao;
        this.conceptSchemeDao = conceptSchemeDao;
        this.relationshipDao = relationshipDao;
        this.skosTranslator = skosTranslator;
    }

    /**
     * Creates all entities defined in an input piece of SKOS.
     *
     * @param skos an {@link InputStream} of the SKOS to be created.
     * @param baseUrl the base URL against which to resolve relative URLs in the SKOS input.
     * @param fileType the file type of the SKOS input.
     * @throws SkosPersistenceException if an error occurs while attempting to persist the SKOS
     *     entities.
     */
    public void create(InputStream skos, String baseUrl, SkosFileType fileType)
            throws SkosPersistenceException {
        Map<String, String> idToUuid = new HashMap<>();
        RdfModel rdfModel = parseSkos(skos, baseUrl, fileType, idToUuid);

        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);

            try {
                for (ConceptModel concept : rdfModel.getConcepts()) {
                    conceptDao.create(concept, connection);
                }
                for (ConceptSchemeModel conceptScheme : rdfModel.getConceptSchemes()) {
                    conceptSchemeDao.create(conceptScheme, connection);
                }
                for (ConceptSemanticRelationModel relationship : rdfModel.getRelationships()) {
                    relationshipDao.create(relationship, connection);
                }

            } catch (SkosPersistenceException e) {
                connection.rollback();
                throw e;
            }

            connection.commit();

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToPersistSkos(e);
        }
    }

    private RdfModel parseSkos(
            InputStream skos, String baseUrl, SkosFileType fileType, Map<String, String> idToUuid) {
        RdfModel rdfModel = skosTranslator.translate(skos, baseUrl, fileType);
        List<ConceptModel> conceptsWithGeneratedUuids =
                rdfModel.getConcepts().stream()
                        .map(originalConcept -> generateUuid(originalConcept, idToUuid))
                        .collect(Collectors.toList());
        List<ConceptSchemeModel> schemesWithGeneratedUuids =
                rdfModel.getConceptSchemes().stream()
                        .map(originalScheme -> generateUuid(originalScheme, idToUuid))
                        .collect(Collectors.toList());
        List<ConceptSemanticRelationModel> relationshipsWithGeneratedUuids =
                rdfModel.getRelationships().stream()
                        .map(originalRelationship -> fixUuids(originalRelationship, idToUuid))
                        .collect(Collectors.toList());
        return new RdfModel(
                conceptsWithGeneratedUuids,
                schemesWithGeneratedUuids,
                relationshipsWithGeneratedUuids);
    }

    private ConceptModel generateUuid(ConceptModel originalConcept, Map<String, String> idToUuid) {
        UUID conceptUuid = UUID.randomUUID();
        idToUuid.put(originalConcept.getId(), conceptUuid.toString());
        return new ConceptModel(
                conceptUuid.toString(),
                originalConcept.getPreferredLabel(),
                originalConcept.getAltLabel(),
                originalConcept.getHiddenLabel(),
                originalConcept.getNote(),
                originalConcept.getChangeNote(),
                originalConcept.getEditorialNote(),
                originalConcept.getExample(),
                originalConcept.getHistoryNote(),
                originalConcept.getScopeNote());
    }

    private ConceptSchemeModel generateUuid(
            ConceptSchemeModel originalScheme, Map<String, String> idToUuid) {
        UUID conceptSchemeUuid = UUID.randomUUID();
        idToUuid.put(originalScheme.getId(), conceptSchemeUuid.toString());
        return new ConceptSchemeModel(conceptSchemeUuid.toString(), originalScheme.getTitle());
    }

    private ConceptSemanticRelationModel fixUuids(
            ConceptSemanticRelationModel originalRelationship, Map<String, String> idToUuid) {
        String sourceUuid = idToUuid.get(originalRelationship.getSourceId());
        String targetUuid = idToUuid.get(originalRelationship.getTargetId());
        return new ConceptSemanticRelationModel(
                sourceUuid,
                targetUuid,
                originalRelationship.getRelation(),
                originalRelationship.isTransitive());
    }

    /**
     * Retrieves the SKOS containing a given concept as well as all entities directly related to
     * that concept.
     *
     * @param id the ID of the concept to retrieve.
     * @param outputFileType the file type in which to write the output SKOS.
     * @return an {@link OutputStream} of the SKOS related to the concept.
     * @throws SkosPersistenceException if an error occurs while attempting to read from the
     *     database.
     */
    public OutputStream getConcept(String id, SkosFileType outputFileType)
            throws SkosPersistenceException {
        try (Connection connection = connectionProvider.getConnection()) {

            ConceptModel concept =
                    conceptDao
                            .read(id, connection)
                            .orElseThrow(() -> SkosPersistenceException.conceptNotFound(id));

            Collection<ConceptSemanticRelationModel> relationships =
                    relationshipDao.getRelationships(concept.getId(), connection);

            RdfModel rdfModel = getRelatedEntities(concept.getId(), relationships, connection);
            rdfModel.getConcepts().add(concept);
            Model model = skosTranslator.translate(rdfModel);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            model.write(outputStream, outputFileType.getFileTypeName());
            return outputStream;

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToGetConcept(id, e);
        }
    }

    private RdfModel getRelatedEntities(
            String originalEntityId,
            Collection<ConceptSemanticRelationModel> relationships,
            Connection connection)
            throws SkosPersistenceException {
        Set<ConceptModel> concepts = new HashSet<>();
        Set<ConceptSchemeModel> conceptSchemes = new HashSet<>();

        for (String id : getRelatedIris(originalEntityId, relationships)) {
            Optional<ConceptModel> concept = conceptDao.read(id, connection);
            if (concept.isPresent()) {
                concepts.add(concept.get());
            } else {
                Optional<ConceptSchemeModel> scheme = conceptSchemeDao.read(id, connection);
                conceptSchemes.add(
                        scheme.orElseThrow(
                                () -> SkosPersistenceException.conceptSchemeNotFound(id)));
            }
        }

        return new RdfModel(concepts, conceptSchemes, relationships);
    }

    private Collection<String> getRelatedIris(
            String originalEntityIri, Collection<ConceptSemanticRelationModel> relationships) {
        return relationships.stream()
                .flatMap(r -> Stream.of(r.getSourceId(), r.getTargetId()))
                .filter(iri -> !originalEntityIri.equals(iri))
                .collect(Collectors.toSet());
    }

    /**
     * Retrieves the SKOS containing a given concept scheme as well as all entities directly related
     * to that scheme.
     *
     * @param id the ID of the concept scheme to retrieve.
     * @param outputFileType the file type in which to write the output SKOS.
     * @return an {@link OutputStream} of the SKOS related to the concept scheme.
     * @throws SkosPersistenceException if an error occurs while attempting to read from the
     *     database.
     */
    public OutputStream getConceptScheme(String id, SkosFileType outputFileType)
            throws SkosPersistenceException {
        try (Connection connection = connectionProvider.getConnection()) {

            ConceptSchemeModel conceptScheme =
                    conceptSchemeDao
                            .read(id, connection)
                            .orElseThrow(() -> SkosPersistenceException.conceptSchemeNotFound(id));

            Collection<ConceptSemanticRelationModel> relationships =
                    relationshipDao.getRelationships(conceptScheme.getId(), connection);

            RdfModel rdfModel =
                    getRelatedEntities(conceptScheme.getId(), relationships, connection);
            rdfModel.getConceptSchemes().add(conceptScheme);
            Model model = skosTranslator.translate(rdfModel);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            model.write(outputStream, outputFileType.getFileTypeName());
            return outputStream;

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToGetConceptScheme(id, e);
        }
    }

    /**
     * Updates all entities defined in an input piece of SKOS.
     *
     * @param skos an {@link InputStream} of the SKOS to be updated.
     * @param baseUrl the base URL against which to resolve relative URLs in the SKOS input.
     * @param fileType the file type of the SKOS input.
     * @throws SkosPersistenceException if an error occurs while attempting to persist the SKOS
     *     entities.
     */
    public void update(InputStream skos, String baseUrl, SkosFileType fileType)
            throws SkosPersistenceException {
        RdfModel rdfModel = skosTranslator.translate(skos, baseUrl, fileType);

        try (Connection connection = connectionProvider.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (ConceptModel conceptModel : rdfModel.getConcepts()) {
                    conceptDao.update(conceptModel, connection);
                }
                for (ConceptSchemeModel conceptScheme : rdfModel.getConceptSchemes()) {
                    conceptSchemeDao.update(conceptScheme, connection);
                }
                for (ConceptSemanticRelationModel relationship : rdfModel.getRelationships()) {
                    relationshipDao.update(relationship, connection);
                }
            } catch (SkosPersistenceException e) {
                connection.rollback();
                throw e;
            }

            connection.commit();

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToPersistSkos(e);
        }
    }

    /**
     * Deletes a given concept scheme, as well as any relationships involving that scheme.
     *
     * @param conceptSchemeId the ID of the scheme to delete.
     * @throws SkosPersistenceException if an error occurs while attempting to delete the scheme.
     */
    public void deleteConceptScheme(String conceptSchemeId) throws SkosPersistenceException {
        try (Connection connection = connectionProvider.getConnection()) {
            try {
                relationshipDao.delete(conceptSchemeId, connection);
                conceptSchemeDao.delete(conceptSchemeId, connection);

            } catch (SkosPersistenceException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToRemoveRelationships(conceptSchemeId, e);
        }
    }

    /**
     * Deletes a concept, as well as any relationships involving that concept.
     *
     * @param conceptId the ID of the concept to delete.
     * @throws SkosPersistenceException if an error occurs while attempting to delete the concept.
     */
    public void deleteConcept(String conceptId) throws SkosPersistenceException {
        try (Connection connection = connectionProvider.getConnection()) {
            try {
                relationshipDao.delete(conceptId, connection);
                conceptDao.delete(conceptId, connection);

            } catch (SkosPersistenceException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToRemoveRelationships(conceptId, e);
        }
    }
}
