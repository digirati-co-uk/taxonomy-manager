package com.digirati.taxonomy.manager.lookup.persistence;

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

public class ConceptDao implements Dao<ConceptModel> {

    private static final Logger logger = LogManager.getLogger(ConceptDao.class);

    private static final String CREATE_TEMPLATE =
            "INSERT INTO concept "
                    + "(iri, preferred_label, alt_label, hidden_label, note, change_note, editorial_note, example, history_note, scope_note) "
                    + "VALUES (?, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB, ?::JSONB)";

    private static final String SELECT_BY_IRI_TEMPLATE = "SELECT * FROM concept WHERE iri=?";

    private static final String SELECT_BY_ID_TEMPLATE = "SELECT * FROM concept WHERE id=?";

    private static final String UPDATE_TEMPLATE =
            "UPDATE concept "
                    + "SET iri=?, preferred_label=?::JSONB, alt_label=?::JSONB, hidden_label=?::JSONB, note=?::JSONB, change_note=?::JSONB, editorial_note=?::JSONB, example=?::JSONB, history_note=?::JSONB, scope_note=?::JSONB "
                    + "WHERE id=?";

    private static final String DELETE_TEMPLATE = "DELETE FROM concept WHERE id=?";

    private final ConnectionProvider connectionProvider;

    ConceptDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Optional<ConceptModel> create(ConceptModel toCreate) {
        logger.info(() -> "Preparing to create concept with IRI=" + toCreate.getIri());

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE);
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_IRI_TEMPLATE)) {

            createStatement.setString(1, toCreate.getIri());
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

            logger.info(() -> "Successfully created concept with IRI=" + toCreate.getIri());

            // TODO should these be atomic?
            // TODO any way of getting the created one by ID? Because then we could swap this whole
            // block for a call to read(id)
            readStatement.setString(1, toCreate.getIri());
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptModel> created = fromResultSet(resultSet);
            // TODO any way of closing this via try with resources?
            resultSet.close();
            if (created != null && !created.isEmpty()) {
                return Optional.of(created.get(created.size() - 1));
            }

        } catch (SQLException e) {
            logger.error(() -> e);
        }

        return Optional.empty();
    }

    private List<ConceptModel> fromResultSet(ResultSet resultSet) throws SQLException {
        List<ConceptModel> concepts = new ArrayList<>();
        while (resultSet.next()) {
            ConceptModel concept =
                    new ConceptModel()
                            .setId(resultSet.getLong("id"))
                            .setIri(resultSet.getString("iri"))
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

    @Override
    public Optional<ConceptModel> read(long primaryKey) {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_ID_TEMPLATE)) {

            readStatement.setLong(1, primaryKey);
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptModel> created = fromResultSet(resultSet);
            // TODO any way of closing this via try with resources?
            resultSet.close();
            if (created != null && !created.isEmpty()) {
                return Optional.of(created.get(created.size() - 1));
            }

        } catch (SQLException e) {
            logger.error(() -> e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ConceptModel> update(ConceptModel toUpdate) {
        logger.info(() -> "Preparing to update concept with IRI=" + toUpdate.getIri());

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            // TODO should the update and read be atomic?
            updateStatement.setString(1, toUpdate.getIri());
            updateStatement.setString(2, toUpdate.getPreferredLabel());
            updateStatement.setString(3, toUpdate.getAltLabel());
            updateStatement.setString(4, toUpdate.getHiddenLabel());
            updateStatement.setString(5, toUpdate.getNote());
            updateStatement.setString(6, toUpdate.getChangeNote());
            updateStatement.setString(7, toUpdate.getEditorialNote());
            updateStatement.setString(8, toUpdate.getExample());
            updateStatement.setString(9, toUpdate.getHistoryNote());
            updateStatement.setString(10, toUpdate.getScopeNote());
            updateStatement.setLong(11, toUpdate.getId());
            int rowsAffected = updateStatement.executeUpdate();

            logger.info(
                    () ->
                            "Successfully updated concept with IRI="
                                    + toUpdate.getIri()
                                    + " - number of rows affected: "
                                    + rowsAffected);

            return read(toUpdate.getId());

        } catch (SQLException e) {
            logger.error(() -> e);
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(long primaryKey) {
        logger.info(() -> "Preparing to delete concept with ID=" + primaryKey);

        try {
            Connection connection = connectionProvider.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TEMPLATE);
            deleteStatement.setLong(1, primaryKey);
            int rowsAffected = deleteStatement.executeUpdate();
            deleteStatement.close();

            logger.info(
                    () ->
                            "Successfully deleted concept with ID="
                                    + primaryKey
                                    + " - number of rows affected: "
                                    + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.error(() -> e);
            return false;
        }
    }
}
