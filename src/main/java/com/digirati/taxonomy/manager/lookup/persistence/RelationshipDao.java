package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * DAO for managing everything to do with persisting and retrieving a {@link
 * ConceptSemanticRelationModel} to/from the database.
 */
class RelationshipDao {

    private static final Logger logger = LogManager.getLogger(RelationshipDao.class);

    private static final String CREATE_TEMPLATE =
            "INSERT INTO concept_semantic_relation "
                    + "(relation, transitive, source_id, target_id) "
                    + "VALUES (?::semantic_relation_type, ?, ?::UUID, ?::UUID)";

    private static final String SELECT_RELATIONSHIP_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE source_id=?::UUID AND target_id=?::UUID";

    private static final String SELECT_RELATED_TO_SOURCE_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE source_id=?::UUID";

    private static final String SELECT_RELATED_TO_TARGET_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE target_id=?::UUID";

    private static final String UPDATE_TEMPLATE =
            "UPDATE concept_semantic_relation SET relation=?::semantic_relation_type, transitive=? WHERE source_id=?::UUID AND target_id=?::UUID";

    private static final String DELETE_RELATIONSHIP_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE source_id=?::UUID AND target_id=?::UUID";

    private static final String DELETE_RELATED_TO_SOURCE_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE source_id=?::UUID";

    private static final String DELETE_RELATED_TO_TARGET_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE target_id=?::UUID";

    /**
     * Persists a new relationship to the database.
     *
     * @param relationship the relationship to persist.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if a relationship between the two entities already exists,
     *     or an error occurs executing the write.
     */
    public void create(ConceptSemanticRelationModel relationship, Connection connection)
            throws SkosPersistenceException {
        if (read(relationship.getSourceId(), relationship.getTargetId(), connection).isPresent()) {
            throw SkosPersistenceException.relationshipAlreadyExists(relationship);
        }

        logger.info("Preparing to create relationship: {}", relationship);

        try (PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, relationship.getRelation().name().toLowerCase());
            createStatement.setBoolean(2, relationship.isTransitive());
            createStatement.setString(3, relationship.getSourceId());
            createStatement.setString(4, relationship.getTargetId());
            createStatement.execute();

            logger.info("Successfully created relationship: {}", relationship);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToCreateRelationship(relationship, e);
        }
    }

    /**
     * Retrieves the relationship between two given entities. Note that the source and target IDs
     * must be provided in the correct order - if no relationship exists in the specified direction
     * but one exists in the opposite direction, this will not be aware of this.
     *
     * @param sourceId the ID of the source entity (also known as the "object" of the relationship)
     * @param targetId the ID of the target entity (also known as the "subject" of the relationship)
     * @param connection a connection to the database.
     * @return an {@link Optional} containing the specified relationship if one exists; an empty
     *     optional if not.
     * @throws SkosPersistenceException if an error occurs while executing the read.
     */
    public Optional<ConceptSemanticRelationModel> read(
            String sourceId, String targetId, Connection connection)
            throws SkosPersistenceException {
        try (PreparedStatement readStatement =
                connection.prepareStatement(SELECT_RELATIONSHIP_TEMPLATE)) {

            readStatement.setString(1, sourceId);
            readStatement.setString(2, targetId);
            ResultSet result = readStatement.executeQuery();
            List<ConceptSemanticRelationModel> retrieved = fromResultSet(result);
            result.close();
            if (!retrieved.isEmpty()) {
                return Optional.of(retrieved.get(retrieved.size() - 1));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.relationshipNotFound(sourceId, targetId, e);
        }
    }

    private List<ConceptSemanticRelationModel> fromResultSet(ResultSet resultSet)
            throws SQLException {
        List<ConceptSemanticRelationModel> relationships = new ArrayList<>();
        while (resultSet.next()) {
            String relationTypeName = resultSet.getString("relation").toUpperCase();
            ConceptSemanticRelationModel relationship =
                    new ConceptSemanticRelationModel(
                            resultSet.getString("source_id"),
                            resultSet.getString("target_id"),
                            SemanticRelationType.valueOf(relationTypeName),
                            resultSet.getBoolean("transitive"));
            relationships.add(relationship);
        }
        return relationships;
    }

    /**
     * Gets all relationships involving a given entity. Note that this will return any relationships
     * in which this entity is either the source or the target.
     *
     * @param sourceId the ID of the entity to get the relationships for.
     * @param connection a connection to the database.
     * @return a collection of all relationships involving the given entity.
     * @throws SkosPersistenceException if an error occurs trying to read from the database.
     */
    public Collection<ConceptSemanticRelationModel> getRelationships(
            String sourceId, Connection connection) throws SkosPersistenceException {
        List<ConceptSemanticRelationModel> relationships = new ArrayList<>();
        relationships.addAll(
                getRelationships(sourceId, connection, SELECT_RELATED_TO_SOURCE_TEMPLATE));
        relationships.addAll(
                getRelationships(sourceId, connection, SELECT_RELATED_TO_TARGET_TEMPLATE));
        return relationships;
    }

    private Collection<ConceptSemanticRelationModel> getRelationships(
            String sourceId, Connection connection, String sqlTemplate)
            throws SkosPersistenceException {

        ResultSet relatedResults = null;
        try (PreparedStatement relatedToSourceStatement =
                connection.prepareStatement(sqlTemplate)) {

            relatedToSourceStatement.setString(1, sourceId);
            relatedResults = relatedToSourceStatement.executeQuery();

            return fromResultSet(relatedResults);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToGetRelationships(sourceId);

        } finally {
            try {
                if (relatedResults != null) {
                    relatedResults.close();
                }
            } catch (SQLException e) {
                logger.error("Unable to close result set", e);
            }
        }
    }

    /**
     * Updates a relationship in the database.
     *
     * @param relationship a model of the updated relationship.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if no relationship exists between the source and target of
     *     the updated relationship, or if something goes wrong executing the update.
     */
    public void update(ConceptSemanticRelationModel relationship, Connection connection)
            throws SkosPersistenceException {
        if (!read(relationship.getSourceId(), relationship.getTargetId(), connection).isPresent()) {
            throw SkosPersistenceException.relationshipNotFound(relationship);
        }

        logger.info("Preparing to update relationship: {}", relationship);

        try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            updateStatement.setString(1, relationship.getRelation().name().toLowerCase());
            updateStatement.setBoolean(2, relationship.isTransitive());
            updateStatement.setString(3, relationship.getSourceId());
            updateStatement.setString(4, relationship.getTargetId());
            updateStatement.executeUpdate();

            logger.info("Successfully updated relationship: {}", relationship);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToUpdateRelationship(relationship, e);
        }
    }

    /**
     * Deletes a relationship between two given entities. Note that the source and target IDs must
     * be provided in the correct order - if no relationship exists in the specified direction but
     * one exists in the opposite direction, the one in the opposite direction will not be removed.
     *
     * @param sourceId the ID of the source entity.
     * @param targetId the ID of the target entity.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if no such relationship exists, or if an error occurs during
     *     the update.
     */
    public void delete(String sourceId, String targetId, Connection connection)
            throws SkosPersistenceException {
        if (!read(sourceId, targetId, connection).isPresent()) {
            throw SkosPersistenceException.relationshipNotFound(sourceId, targetId);
        }

        logger.info("Preparing to delete relationship between {} and {}", sourceId, targetId);

        try (PreparedStatement deleteStatement =
                connection.prepareStatement(DELETE_RELATIONSHIP_TEMPLATE)) {

            deleteStatement.setString(1, sourceId);
            deleteStatement.setString(2, targetId);
            int rowsAffected = deleteStatement.executeUpdate();

            logger.info(
                    "Successfully deleted relationship between {} and {} - number of rows affected: {}",
                    sourceId,
                    targetId,
                    rowsAffected);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToRemoveRelationship(sourceId, targetId, e);
        }
    }

    /**
     * Deletes all relationships involving a given entity.
     *
     * @param id the ID of the entity for which to remove all relationships.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if an error occurs while executing the update.
     */
    public void delete(String id, Connection connection) throws SkosPersistenceException {
        logger.info("Preparing to delete all relationships involving {}", id);

        try (PreparedStatement deleteBySourceIri =
                        connection.prepareStatement(DELETE_RELATED_TO_SOURCE_TEMPLATE);
                PreparedStatement deleteByTargetIri =
                        connection.prepareStatement(DELETE_RELATED_TO_TARGET_TEMPLATE)) {

            connection.setAutoCommit(false);

            deleteBySourceIri.setString(1, id);
            int sourceRowsAffected = deleteBySourceIri.executeUpdate();

            deleteByTargetIri.setString(1, id);
            int targetRowsAffected = deleteByTargetIri.executeUpdate();

            connection.commit();

            logger.info(
                    "Successfully deleted all relationships involving {} - number of source relationships deleted: {}, number of target relationships deleted: {}",
                    id,
                    sourceRowsAffected,
                    targetRowsAffected);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToRemoveRelationships(id, e);
        }
    }
}
