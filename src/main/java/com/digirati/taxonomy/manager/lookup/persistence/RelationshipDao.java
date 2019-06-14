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

    public void create(ConceptSemanticRelationModel relationship, Connection connection)
            throws SkosPersistenceException {
        if (read(relationship.getSourceId(), relationship.getTargetId(), connection).isPresent()) {
            throw SkosPersistenceException.relationshipAlreadyExists(relationship);
        }

        logger.info("Preparing to create relationship: " + relationship);

        try (PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, relationship.getRelation().name().toLowerCase());
            createStatement.setBoolean(2, relationship.isTransitive());
            createStatement.setString(3, relationship.getSourceId());
            createStatement.setString(4, relationship.getTargetId());
            createStatement.execute();

            logger.info("Successfully created relationship: " + relationship);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToCreateRelationship(relationship, e);
        }
    }

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

    public Collection<ConceptSemanticRelationModel> getRelationships(
            String sourceId, Connection connection) throws SkosPersistenceException {
        List<ConceptSemanticRelationModel> relationships = new ArrayList<>();

        ResultSet relatedToSourceResults = null;
        ResultSet relatedToTargetResults = null;
        try (PreparedStatement relatedToSourceStatement =
                        connection.prepareStatement(SELECT_RELATED_TO_SOURCE_TEMPLATE);
                PreparedStatement relatedToTargetStatement =
                        connection.prepareStatement(SELECT_RELATED_TO_TARGET_TEMPLATE)) {

            relatedToSourceStatement.setString(1, sourceId);
            relatedToTargetStatement.setString(1, sourceId);
            relatedToSourceResults = relatedToSourceStatement.executeQuery();
            relatedToTargetResults = relatedToTargetStatement.executeQuery();

            relationships.addAll(fromResultSet(relatedToSourceResults));
            relationships.addAll(fromResultSet(relatedToTargetResults));

            return relationships;

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToGetRelationships(sourceId);

        } finally {
            try {
                if (relatedToSourceResults != null) {
                    relatedToSourceResults.close();
                }
                if (relatedToTargetResults != null) {
                    relatedToTargetResults.close();
                }
            } catch (SQLException e) {
                logger.error("Unable to close result set", e);
            }
        }
    }

    public void update(ConceptSemanticRelationModel relationship, Connection connection)
            throws SkosPersistenceException {
        if (!read(relationship.getSourceId(), relationship.getTargetId(), connection).isPresent()) {
            throw SkosPersistenceException.relationshipNotFound(relationship);
        }

        logger.info("Preparing to update relationship: " + relationship);

        try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            updateStatement.setString(1, relationship.getRelation().name().toLowerCase());
            updateStatement.setBoolean(2, relationship.isTransitive());
            updateStatement.setString(3, relationship.getSourceId());
            updateStatement.setString(4, relationship.getTargetId());
            updateStatement.executeUpdate();

            logger.info("Successfully updated relationship: " + relationship);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToUpdateRelationship(relationship, e);
        }
    }

    public void delete(String sourceId, String targetId, Connection connection)
            throws SkosPersistenceException {
        if (!read(sourceId, targetId, connection).isPresent()) {
            throw SkosPersistenceException.relationshipNotFound(sourceId, targetId);
        }

        logger.info("Preparing to delete relationship between " + sourceId + " and " + targetId);

        try (PreparedStatement deleteStatement =
                connection.prepareStatement(DELETE_RELATIONSHIP_TEMPLATE)) {

            deleteStatement.setString(1, sourceId);
            deleteStatement.setString(2, targetId);
            int rowsAffected = deleteStatement.executeUpdate();

            logger.info(
                    "Successfully deleted relationship between "
                            + sourceId
                            + " and "
                            + targetId
                            + " - number of rows affected: "
                            + rowsAffected);

        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public void delete(String id, Connection connection) {
        logger.info("Preparing to delete all relationships involving " + id);

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
                    "Successfully deleted all relationships involving "
                            + id
                            + " - number of source relationships deleted: "
                            + sourceRowsAffected
                            + ", number of target relationships deleted: "
                            + targetRowsAffected);

        } catch (SQLException e) {
            logger.error(e);
        }
    }
}
