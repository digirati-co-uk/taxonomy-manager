package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO for managing everything to do with persisting and retrieving a {@link ConceptSchemeModel}
 * to/from the database.
 */
class ConceptSchemeDao {

    private static final Logger logger = LogManager.getLogger(ConceptSchemeDao.class);

    private static final String CREATE_TEMPLATE =
            "INSERT INTO concept_scheme (id, title) VALUES (?::UUID, ?)";

    private static final String SELECT_TEMPLATE = "SELECT * FROM concept_scheme WHERE id=?::UUID";

    private static final String UPDATE_TEMPLATE =
            "UPDATE concept_scheme SET title=? WHERE id=?::UUID";

    private static final String DELETE_TEMPLATE = "DELETE FROM concept_scheme WHERE id=?::UUID";

    /**
     * Persists a new concept scheme to the database.
     *
     * @param toCreate the concept scheme to persist.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if the concept scheme already exists, or an error occurs
     *     executing the write.
     */
    public void create(ConceptSchemeModel toCreate, Connection connection)
            throws SkosPersistenceException {
        logger.info("Preparing to create concept scheme with ID={}", toCreate.getId());

        if (toCreate.getId() != null && read(toCreate.getId(), connection).isPresent()) {
            throw SkosPersistenceException.conceptSchemeAlreadyExists(toCreate.getId());
        }

        try (PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, toCreate.getId());
            createStatement.setString(2, toCreate.getTitle());
            createStatement.execute();

            logger.info("Successfully created concept with ID={}", toCreate.getId());

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToCreateConceptScheme(toCreate.getId(), e);
        }
    }

    private List<ConceptSchemeModel> fromResultSet(ResultSet resultSet) throws SQLException {
        List<ConceptSchemeModel> conceptSchemes = new ArrayList<>();
        while (resultSet.next()) {
            ConceptSchemeModel conceptScheme =
                    new ConceptSchemeModel(resultSet.getString("id"), resultSet.getString("title"));
            conceptSchemes.add(conceptScheme);
        }
        return conceptSchemes;
    }

    /**
     * Retrieves a concept scheme with a given ID from the database.
     *
     * @param id the ID of the concept scheme to retrieve.
     * @param connection a connection to the database.
     * @return an {@link Optional} containing the retrieved concept scheme if present in the
     *     database; an empty Optional if not.
     */
    public Optional<ConceptSchemeModel> read(String id, Connection connection) {
        try (PreparedStatement readStatement = connection.prepareStatement(SELECT_TEMPLATE)) {

            readStatement.setString(1, id);
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptSchemeModel> readResults = fromResultSet(resultSet);
            resultSet.close();
            if (!readResults.isEmpty()) {
                return Optional.of(readResults.get(readResults.size() - 1));
            }

        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    /**
     * Updates a pre-existing concept scheme in the database.
     *
     * @param toUpdate a model of the updated concept scheme.
     * @param connection a connection to the database
     * @throws SkosPersistenceException if no ID is supplied against which to update the row, the
     *     supplied ID does not correspond to any row in the database, or an error occurs executing
     *     the write.
     */
    public void update(ConceptSchemeModel toUpdate, Connection connection)
            throws SkosPersistenceException {
        logger.info("Preparing to update concept scheme with ID={}", toUpdate.getId());

        if (toUpdate.getId() == null || !read(toUpdate.getId(), connection).isPresent()) {
            throw SkosPersistenceException.conceptSchemeNotFound(toUpdate.getId());
        }

        try (PreparedStatement createStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            createStatement.setString(1, toUpdate.getTitle());
            createStatement.setString(2, toUpdate.getId());
            createStatement.execute();

            logger.info("Successfully updated concept with ID={}", toUpdate.getId());

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToUpdateConceptScheme(toUpdate.getId(), e);
        }
    }

    /**
     * Deletes a concept scheme with a given ID from the database.
     *
     * @param id the ID of the concept scheme to delete.
     * @param connection a connection to the database.
     * @throws SkosPersistenceException if no such concept scheme exists to be deleted, or if an
     *     error occurs while executing the update.
     */
    public void delete(String id, Connection connection) throws SkosPersistenceException {
        logger.info("Preparing to delete concept scheme with ID={}", id);

        if (!read(id, connection).isPresent()) {
            throw SkosPersistenceException.conceptSchemeNotFound(id);
        }

        try (PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TEMPLATE)) {
            deleteStatement.setString(1, id);
            int rowsAffected = deleteStatement.executeUpdate();

            logger.info(
                    "Successfully deleted concept scheme with ID={} - number of rows affected: {}",
                    id,
                    rowsAffected);

        } catch (SQLException e) {
            logger.error(e);
            throw SkosPersistenceException.unableToDeleteConceptScheme(id, e);
        }
    }
}
