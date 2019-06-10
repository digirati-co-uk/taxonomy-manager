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

    private static final String SELECT_BY_IRI_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE source_id=?::UUID AND target_id=?::UUID";

    private static final String RELATED_TO_SOURCE_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE source_id=?::UUID";

    private static final String RELATED_TO_TARGET_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE target_id=?::UUID";

    private static final String UPDATE_BY_IRI_TEMPLATE =
            "UPDATE concept_semantic_relation SET relation=?::semantic_relation_type, transitive=? WHERE source_id=?::UUID AND target_id=?::UUID";

    private static final String DELETE_BY_IRI_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE source_id=?::UUID AND target_id=?::UUID";

    private static final String DELETE_BY_SOURCE_IRI_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE source_id=?::UUID";

    private static final String DELETE_BY_TARGET_IRI_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE target_id=?::UUID";

    private final ConnectionProvider connectionProvider;

    RelationshipDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public ConceptSemanticRelationModel create(ConceptSemanticRelationModel relationship)
            throws SkosPersistenceException {
        if (read(relationship.getSourceId(), relationship.getTargetId()).isPresent()) {
            throw SkosPersistenceException.relationshipAlreadyExists(relationship);
        }

        logger.info(() -> "Preparing to create relationship: " + relationship);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, relationship.getRelation().name().toLowerCase());
            createStatement.setBoolean(2, relationship.isTransitive());
            createStatement.setString(3, relationship.getSourceId());
            createStatement.setString(4, relationship.getTargetId());
            createStatement.execute();

            logger.info(() -> "Successfully created relationship: " + relationship);

            return read(relationship.getSourceId(), relationship.getTargetId())
                    .orElseThrow(() -> SkosPersistenceException.unableToCreateRelationship(relationship));

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.unableToCreateRelationship(relationship, e);
        }
    }

    // TODO can multiple relationships exist between the same two things in the same direction?
    public Optional<ConceptSemanticRelationModel> read(String sourceId, String targetId)
            throws SkosPersistenceException {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_IRI_TEMPLATE)) {

            readStatement.setString(1, sourceId);
            readStatement.setString(2, targetId);
            ResultSet result = readStatement.executeQuery();
            List<ConceptSemanticRelationModel> retrieved = fromResultSet(result);
            result.close();
            if (retrieved != null && !retrieved.isEmpty()) {
                return Optional.of(retrieved.get(retrieved.size() - 1));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.relationshipNotFound(sourceId, targetId, e);
        }
    }

    private List<ConceptSemanticRelationModel> fromResultSet(ResultSet resultSet)
            throws SQLException {
        List<ConceptSemanticRelationModel> relationships = new ArrayList<>();
        while (resultSet.next()) {
            String relationTypeName = resultSet.getString("relation").toUpperCase();
            ConceptSemanticRelationModel relationship =
                    new ConceptSemanticRelationModel()
                            .setRelation(SemanticRelationType.valueOf(relationTypeName))
                            .setTransitive(resultSet.getBoolean("transitive"))
                            .setSourceId(resultSet.getString("source_id"))
                            .setTargetId(resultSet.getString("target_id"));
            relationships.add(relationship);
        }
        return relationships;
    }

    public Collection<ConceptSemanticRelationModel> getRelationships(String sourceId)
            throws SkosPersistenceException {
        List<ConceptSemanticRelationModel> relationships = new ArrayList<>();

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement relatedToSourceStatement =
                        connection.prepareStatement(RELATED_TO_SOURCE_TEMPLATE);
                PreparedStatement relatedToTargetStatement =
                        connection.prepareStatement(RELATED_TO_TARGET_TEMPLATE)) {

            connection.setAutoCommit(false);

            relatedToSourceStatement.setString(1, sourceId);
            relatedToTargetStatement.setString(1, sourceId);
            ResultSet relatedToSourceResults = relatedToSourceStatement.executeQuery();
            ResultSet relatedToTargetResults = relatedToTargetStatement.executeQuery();

            connection.commit();

            relationships.addAll(fromResultSet(relatedToSourceResults));
            relationships.addAll(fromResultSet(relatedToTargetResults));
            relatedToSourceResults.close();
            relatedToTargetResults.close();

            return relationships;

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.unableToGetRelationships(sourceId);
        }
    }

    public ConceptSemanticRelationModel update(ConceptSemanticRelationModel relationship)
            throws SkosPersistenceException {
        if (!read(relationship.getSourceId(), relationship.getTargetId()).isPresent()) {
            throw SkosPersistenceException.relationshipNotFound(relationship);
        }

        logger.info(() -> "Preparing to update relationship: " + relationship);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement updateStatement =
                        connection.prepareStatement(UPDATE_BY_IRI_TEMPLATE)) {

            updateStatement.setString(1, relationship.getRelation().name().toLowerCase());
            updateStatement.setBoolean(2, relationship.isTransitive());
            updateStatement.setString(3, relationship.getSourceId());
            updateStatement.setString(4, relationship.getTargetId());
            updateStatement.executeUpdate();

            logger.info(() -> "Successfully updated relationship: " + relationship);

            return read(relationship.getSourceId(), relationship.getTargetId())
                    .orElseThrow(() -> SkosPersistenceException.unableToUpdateRelationship(relationship));

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.unableToUpdateRelationship(relationship, e);
        }
    }

    public boolean delete(String sourceId, String targetId) throws SkosPersistenceException {
        if (!read(sourceId, targetId).isPresent()) {
            throw SkosPersistenceException.relationshipNotFound(sourceId, targetId);
        }

        logger.info(
                () ->
                        "Preparing to delete relationship between "
                                + sourceId
                                + " and "
                                + targetId);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(DELETE_BY_IRI_TEMPLATE)) {

            deleteStatement.setString(1, sourceId);
            deleteStatement.setString(2, targetId);
            int rowsAffected = deleteStatement.executeUpdate();

            logger.info(
                    () ->
                            "Successfully deleted relationship between "
                                    + sourceId
                                    + " and "
                                    + targetId
                                    + " - number of rows affected: "
                                    + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error(() -> e);
            return false;
        }
    }

    public boolean delete(String id) {
        logger.info(() -> "Preparing to delete all relationships involving " + id);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteBySourceIri =
                        connection.prepareStatement(DELETE_BY_SOURCE_IRI_TEMPLATE);
                PreparedStatement deleteByTargetIri =
                        connection.prepareStatement(DELETE_BY_TARGET_IRI_TEMPLATE)) {

            connection.setAutoCommit(false);

            deleteBySourceIri.setString(1, id);
            int sourceRowsAffected = deleteBySourceIri.executeUpdate();

            deleteByTargetIri.setString(1, id);
            int targetRowsAffected = deleteByTargetIri.executeUpdate();

            connection.commit();

            logger.info(
                    () ->
                            "Successfully deleted all relationships involving "
                                    + id
                                    + " - number of source relationships deleted: "
                                    + sourceRowsAffected
                                    + ", number of target relationships deleted: "
                                    + targetRowsAffected);

            return (sourceRowsAffected + targetRowsAffected) > 0;

        } catch (SQLException e) {
            logger.error(() -> e);
            return false;
        }
    }
}
