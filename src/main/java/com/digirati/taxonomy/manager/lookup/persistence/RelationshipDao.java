package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.RelationshipAlreadyExistsException;
import com.digirati.taxonomy.manager.lookup.exception.RelationshipNotFoundException;
import com.digirati.taxonomy.manager.lookup.exception.UnableToCreateRelationshipException;
import com.digirati.taxonomy.manager.lookup.exception.UnableToUpdateRelationshipException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class RelationshipDao {

    private static final Logger logger = LogManager.getLogger(RelationshipDao.class);

    private static final String CREATE_TEMPLATE =
            "INSERT INTO concept_semantic_relation "
                    + "(relation, transitive, source_id, target_id, source_iri, target_iri) "
                    + "VALUES (?::semantic_relation_type, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_IRI_TEMPLATE =
            "SELECT * FROM concept_semantic_relation WHERE source_iri=? AND target_iri=?";

    private static final String UPDATE_BY_IRI_TEMPLATE =
            "UPDATE concept_semantic_relation SET relation=?::semantic_relation_type, transitive=? WHERE source_iri=? AND target_iri=?";

    private static final String DELETE_BY_IRI_TEMPLATE =
            "DELETE FROM concept_semantic_relation WHERE source_iri=? AND target_iri=?";

    private final ConnectionProvider connectionProvider;

    RelationshipDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public ConceptSemanticRelationModel create(ConceptSemanticRelationModel relationship)
            throws RelationshipAlreadyExistsException, RelationshipNotFoundException,
                    UnableToCreateRelationshipException {
        if (read(relationship.getSourceIri(), relationship.getTargetIri()).isPresent()) {
            throw new RelationshipAlreadyExistsException(relationship);
        }

        logger.info(() -> "Preparing to create relationship: " + relationship);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, relationship.getRelation().name().toLowerCase());
            createStatement.setBoolean(2, relationship.isTransitive());
            createStatement.setLong(3, relationship.getSourceId());
            createStatement.setLong(4, relationship.getTargetId());
            createStatement.setString(5, relationship.getSourceIri());
            createStatement.setString(6, relationship.getTargetIri());
            createStatement.execute();

            logger.info(() -> "Successfully created relationship: " + relationship);

            return read(relationship.getSourceIri(), relationship.getTargetIri())
                    .orElseThrow(() -> new UnableToCreateRelationshipException(relationship));

        } catch (SQLException e) {
            logger.error(() -> e);
            throw new UnableToCreateRelationshipException(relationship, e);
        }
    }

    // TODO can multiple relationships exist between the same two things?
    public Optional<ConceptSemanticRelationModel> read(String sourceIri, String targetIri)
            throws RelationshipNotFoundException {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_IRI_TEMPLATE)) {

            readStatement.setString(1, sourceIri);
            readStatement.setString(2, targetIri);
            ResultSet result = readStatement.executeQuery();
            List<ConceptSemanticRelationModel> retrieved = fromResultSet(result);
            result.close();
            if (retrieved != null && !retrieved.isEmpty()) {
                return Optional.of(retrieved.get(retrieved.size() - 1));
            }
            return Optional.empty();

        } catch (SQLException e) {
            logger.error(() -> e);
            throw new RelationshipNotFoundException(sourceIri, targetIri, e);
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
                            .setSourceId(resultSet.getLong("source_id"))
                            .setTargetId(resultSet.getLong("target_id"))
                            .setSourceIri(resultSet.getString("source_iri"))
                            .setTargetIri(resultSet.getString("target_iri"));
            relationships.add(relationship);
        }
        return relationships;
    }

    public ConceptSemanticRelationModel update(ConceptSemanticRelationModel relationship)
            throws RelationshipNotFoundException, UnableToUpdateRelationshipException {
        if (!read(relationship.getSourceIri(), relationship.getTargetIri()).isPresent()) {
            throw new RelationshipNotFoundException(relationship);
        }

        logger.info(() -> "Preparing to update relationship: " + relationship);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement updateStatement =
                        connection.prepareStatement(UPDATE_BY_IRI_TEMPLATE)) {

            updateStatement.setString(1, relationship.getRelation().name().toLowerCase());
            updateStatement.setBoolean(2, relationship.isTransitive());
            updateStatement.setString(3, relationship.getSourceIri());
            updateStatement.setString(4, relationship.getTargetIri());
            updateStatement.executeUpdate();

            logger.info(() -> "Successfully updated relationship: " + relationship);

            return read(relationship.getSourceIri(), relationship.getTargetIri())
                    .orElseThrow(() -> new UnableToUpdateRelationshipException(relationship));

        } catch (SQLException e) {
            logger.error(() -> e);
            throw new UnableToUpdateRelationshipException(relationship, e);
        }
    }

    public boolean delete(String sourceIri, String targetIri) throws RelationshipNotFoundException {
        if (!read(sourceIri, targetIri).isPresent()) {
            throw new RelationshipNotFoundException(sourceIri, targetIri);
        }

        logger.info(
                () ->
                        "Preparing to delete relationship between "
                                + sourceIri
                                + " and "
                                + targetIri);

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(DELETE_BY_IRI_TEMPLATE)) {

            deleteStatement.setString(1, sourceIri);
            deleteStatement.setString(2, targetIri);
            int rowsAffected = deleteStatement.executeUpdate();

            logger.info(
                    () ->
                            "Successfully deleted relationship between "
                                    + sourceIri
                                    + " and "
                                    + targetIri
                                    + " - number of rows affected: "
                                    + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error(() -> e);
            return false;
        }
    }
}
