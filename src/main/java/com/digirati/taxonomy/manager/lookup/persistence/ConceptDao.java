package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConceptDao {

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

    private final ConnectionProvider connectionProvider;

    ConceptDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Optional<ConceptModel> create(ConceptModel toCreate) throws SkosPersistenceException {
        logger.info(() -> "Preparing to create concept with ID=" + toCreate.getId());

        if (toCreate.getId() != null && read(toCreate.getId()).isPresent()) {
            throw SkosPersistenceException.conceptAlreadyExists(toCreate.getId());
        }

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, toCreate.getId());
            createStatement.setString(2, toCreate.getPreferredLabel());
            createStatement.setString(3, toCreate.getAltLabel());
            createStatement.setString(4, toCreate.getHiddenLabel());
            createStatement.setString(5, toCreate.getNote());
            createStatement.setString(6, toCreate.getChangeNote());
            createStatement.setString(7, toCreate.getEditorialNote());
            createStatement.setString(8, toCreate.getExample());
            createStatement.setString(9, toCreate.getHistoryNote());
            createStatement.setString(10, toCreate.getScopeNote());
            createStatement.execute();

            logger.info(() -> "Successfully created concept with IRI=" + toCreate.getId());

            return read(toCreate.getId());

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.unableToCreateConcept(toCreate.getId(), e);
        }
    }

    private List<ConceptModel> fromResultSet(ResultSet resultSet) throws SQLException {
        List<ConceptModel> concepts = new ArrayList<>();
        while (resultSet.next()) {
            ConceptModel concept =
                    new ConceptModel()
                            .setId(resultSet.getString("id"))
                            .setPreferredLabel(resultSet.getString("preferred_label"))
                            .setAltLabel(resultSet.getString("alt_label"))
                            .setHiddenLabel(resultSet.getString("hidden_label"))
                            .setNote(resultSet.getString("note"))
                            .setChangeNote(resultSet.getString("change_note"))
                            .setEditorialNote(resultSet.getString("editorial_note"))
                            .setExample(resultSet.getString("example"))
                            .setHistoryNote(resultSet.getString("history_note"))
                            .setScopeNote(resultSet.getString("scope_note"));
            concepts.add(concept);
        }
        return concepts;
    }

    public Optional<ConceptModel> read(String id) {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_IRI_TEMPLATE)) {

            readStatement.setString(1, id);
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptModel> created = fromResultSet(resultSet);
            resultSet.close();
            if (!created.isEmpty()) {
                return Optional.of(created.get(created.size() - 1));
            }

        } catch (SQLException e) {
            logger.error(() -> e);
        }

        return Optional.empty();
    }

    public Optional<ConceptModel> update(ConceptModel toUpdate) throws SkosPersistenceException {
        logger.info(() -> "Preparing to update concept with IRI=" + toUpdate.getId());

        if (toUpdate.getId() == null) {
            // TODO this could be better
            throw SkosPersistenceException.conceptNotFound(toUpdate.getId());
        }

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            updateStatement.setString(1, toUpdate.getPreferredLabel());
            updateStatement.setString(2, toUpdate.getAltLabel());
            updateStatement.setString(3, toUpdate.getHiddenLabel());
            updateStatement.setString(4, toUpdate.getNote());
            updateStatement.setString(5, toUpdate.getChangeNote());
            updateStatement.setString(6, toUpdate.getEditorialNote());
            updateStatement.setString(7, toUpdate.getExample());
            updateStatement.setString(8, toUpdate.getHistoryNote());
            updateStatement.setString(9, toUpdate.getScopeNote());
            updateStatement.setString(10, toUpdate.getId());
            int rowsAffected = updateStatement.executeUpdate();

            logger.info(
                    () ->
                            "Successfully updated concept with ID="
                                    + toUpdate.getId()
                                    + " - number of rows affected: "
                                    + rowsAffected);

            return read(toUpdate.getId());

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.unableToUpdateConcept(toUpdate.getId(), e);
        }
    }

    public boolean delete(String id) throws SkosPersistenceException {
        logger.info(() -> "Preparing to delete concept with ID=" + id);

        if (!read(id).isPresent()) {
            throw SkosPersistenceException.conceptNotFound(id);
        }

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TEMPLATE)) {
            deleteStatement.setString(1, id);
            int rowsAffected = deleteStatement.executeUpdate();
            deleteStatement.close();

            logger.info(
                    () ->
                            "Successfully deleted concept with ID="
                                    + id
                                    + " - number of rows affected: "
                                    + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error(() -> e);
            return false;
        }
    }
}
