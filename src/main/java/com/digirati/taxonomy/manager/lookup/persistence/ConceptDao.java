package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for managing everything to do with persisting and retrieving a {@link ConceptModel} to/from
 * the database.
 */
class ConceptDao {

    private static final Logger logger = LogManager.getLogger(ConceptDao.class);

    private static final String CREATE_TEMPLATE =
            "INSERT INTO concept "
                    + "(id, preferred_label, alt_label, hidden_label, note, change_note, editorial_note, example, history_note, scope_note) "
                    + "VALUES (?::UUID, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB)";

    private static final String SELECT_BY_IRI_TEMPLATE = "SELECT * FROM concept WHERE id=?::UUID";

    private static final String UPDATE_TEMPLATE =
            "UPDATE concept "
                    + "SET preferred_label=?::JSONB, alt_label=?::JSONB, hidden_label=?::JSONB, note=?::JSONB, change_note=?::JSONB, editorial_note=?::JSONB, example=?::JSONB, history_note=?::JSONB, scope_note=?::JSONB "
                    + "WHERE id=?::UUID";

    private static final String DELETE_TEMPLATE = "DELETE FROM concept WHERE id=?::UUID";

    /**
     * Persists a new concept to the database.
     *
     * @param toCreate the concept to persist.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if the concept already exists, or an error occurs executing
     *     the write.
     */
    public void create(ConceptModel toCreate, Connection connection)
            throws SkosPersistenceException {
        logger.info("Preparing to create concept with ID={}", toCreate.getId());

        if (toCreate.getId() != null && read(toCreate.getId(), connection).isPresent()) {
            throw SkosPersistenceException.conceptAlreadyExists(toCreate.getId());
        }

        try (PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, toCreate.getId());
            createStatement.setString(2, toCreate.getPreferredLabel().toString());
            createStatement.setString(3, toCreate.getAltLabel().toString());
            createStatement.setString(4, toCreate.getHiddenLabel().toString());
            createStatement.setString(5, toCreate.getNote().toString());
            createStatement.setString(6, toCreate.getChangeNote().toString());
            createStatement.setString(7, toCreate.getEditorialNote().toString());
            createStatement.setString(8, toCreate.getExample().toString());
            createStatement.setString(9, toCreate.getHistoryNote().toString());
            createStatement.setString(10, toCreate.getScopeNote().toString());
            createStatement.execute();

            logger.info("Successfully created concept with ID={}", toCreate.getId());

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToCreateConcept(toCreate.getId(), e);
        }
    }

    private List<ConceptModel> fromResultSet(ResultSet resultSet) throws SQLException, IOException {
        List<ConceptModel> concepts = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        while (resultSet.next()) {
            ConceptModel concept =
                    new ConceptModel(
                            resultSet.getString("id"),
                            objectMapper.readTree(resultSet.getString("preferred_label")),
                            objectMapper.readTree(resultSet.getString("alt_label")),
                            objectMapper.readTree(resultSet.getString("hidden_label")),
                            objectMapper.readTree(resultSet.getString("note")),
                            objectMapper.readTree(resultSet.getString("change_note")),
                            objectMapper.readTree(resultSet.getString("editorial_note")),
                            objectMapper.readTree(resultSet.getString("example")),
                            objectMapper.readTree(resultSet.getString("history_note")),
                            objectMapper.readTree(resultSet.getString("scope_note")));
            concepts.add(concept);
        }
        return concepts;
    }

    /**
     * Retrieves a concept with a given ID from the database.
     *
     * @param id the ID of the concept to retrieve.
     * @param connection a connection to the database.
     * @return an {@link Optional} containing the retrieved concept if present in the database; an
     *     empty Optional if not.
     */
    public Optional<ConceptModel> read(String id, Connection connection)
            throws SkosPersistenceException {
        try (PreparedStatement readStatement =
                connection.prepareStatement(SELECT_BY_IRI_TEMPLATE)) {

            readStatement.setString(1, id);
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptModel> created = fromResultSet(resultSet);
            resultSet.close();
            if (!created.isEmpty()) {
                return Optional.of(created.get(created.size() - 1));
            }
            return Optional.empty();

        } catch (SQLException | IOException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToGetConcept(id, e);
        }
    }

    /**
     * Updates a pre-existing concept in the database.
     *
     * @param toUpdate a model of the updated concept.
     * @param connection a connection to the database
     * @throws SkosPersistenceException if no ID is supplied against which to update the row, the
     *     supplied ID does not correspond to any row in the database, or an error occurs executing
     *     the write.
     */
    public void update(ConceptModel toUpdate, Connection connection)
            throws SkosPersistenceException {
        logger.info("Preparing to update concept with ID={}", toUpdate.getId());

        if (toUpdate.getId() == null || !read(toUpdate.getId(), connection).isPresent()) {
            throw SkosPersistenceException.conceptNotFound(toUpdate.getId());
        }

        try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            updateStatement.setString(1, toUpdate.getPreferredLabel().toString());
            updateStatement.setString(2, toUpdate.getAltLabel().toString());
            updateStatement.setString(3, toUpdate.getHiddenLabel().toString());
            updateStatement.setString(4, toUpdate.getNote().toString());
            updateStatement.setString(5, toUpdate.getChangeNote().toString());
            updateStatement.setString(6, toUpdate.getEditorialNote().toString());
            updateStatement.setString(7, toUpdate.getExample().toString());
            updateStatement.setString(8, toUpdate.getHistoryNote().toString());
            updateStatement.setString(9, toUpdate.getScopeNote().toString());
            updateStatement.setString(10, toUpdate.getId());
            int rowsAffected = updateStatement.executeUpdate();

            logger.info(
                    "Successfully updated concept with ID={} - number of rows affected: {}",
                    toUpdate.getId(),
                    rowsAffected);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToUpdateConcept(toUpdate.getId(), e);
        }
    }

    /**
     * Deletes a concept with a given ID from the database.
     *
     * @param id the ID of the concept to delete.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if no such concept exists to be deleted, or if an error
     *     occurs while executing the update.
     */
    public void delete(String id, Connection connection) throws SkosPersistenceException {
        logger.info("Preparing to delete concept with ID={}", id);

        if (!read(id, connection).isPresent()) {
            throw SkosPersistenceException.conceptNotFound(id);
        }

        try (PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TEMPLATE)) {
            deleteStatement.setString(1, id);
            int rowsAffected = deleteStatement.executeUpdate();
            deleteStatement.close();

            logger.info(
                    "Successfully deleted concept with ID={} - number of rows affected: {}",
                    id,
                    rowsAffected);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToDeleteConcept(id, e);
        }
    }
}
