package com.digirati.taxonomy.manager.lookup.persistence;

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

public class ConceptSchemeDao {

    private static final Logger logger = LogManager.getLogger(ConceptSchemeDao.class);

    private static final String CREATE_TEMPLATE = "INSERT INTO concept_scheme (iri) VALUES (?)";

    private static final String SELECT_BY_IRI_TEMPLATE = "SELECT * FROM concept_scheme WHERE iri=?";

    private static final String SELECT_BY_ID_TEMPLATE = "SELECT * FROM concept_scheme WHERE id=?";

    private static final String UPDATE_TEMPLATE = "UPDATE concept_scheme SET iri=? WHERE id=?";

    private static final String DELETE_TEMPLATE = "DELETE FROM concept_scheme WHERE id=?";

    private final ConnectionProvider connectionProvider;

    ConceptSchemeDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Optional<ConceptSchemeModel> create(ConceptSchemeModel toCreate) {
        logger.info(() -> "Preparing to create concept scheme with IRI=" + toCreate.getIri());

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE);
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_IRI_TEMPLATE)) {

            createStatement.setString(1, toCreate.getIri());
            createStatement.execute();

            logger.info(() -> "Successfully created concept with IRI=" + toCreate.getIri());

            readStatement.setString(1, toCreate.getIri());
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptSchemeModel> created = fromResultSet(resultSet);
            resultSet.close();
            if (created != null && !created.isEmpty()) {
                return Optional.of(created.get(created.size() - 1));
            }

        } catch (SQLException e) {
            logger.error(() -> e);
        }

        return Optional.empty();
    }

    private List<ConceptSchemeModel> fromResultSet(ResultSet resultSet) throws SQLException {
        List<ConceptSchemeModel> conceptSchemes = new ArrayList<>();
        while (resultSet.next()) {
            ConceptSchemeModel conceptScheme =
                    new ConceptSchemeModel()
                            .setId(resultSet.getLong("id"))
                            .setIri(resultSet.getString("iri"));
            conceptSchemes.add(conceptScheme);
        }
        return conceptSchemes;
    }

    public Optional<ConceptSchemeModel> read(long primaryKey) {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_ID_TEMPLATE)) {

            readStatement.setLong(1, primaryKey);
            ResultSet resultSet = readStatement.executeQuery();
            List<ConceptSchemeModel> created = fromResultSet(resultSet);
            resultSet.close();
            if (created != null && !created.isEmpty()) {
                return Optional.of(created.get(created.size() - 1));
            }

        } catch (SQLException e) {
            logger.error(() -> e);
        }

        return Optional.empty();
    }

    public Optional<ConceptSchemeModel> update(ConceptSchemeModel toUpdate) {
        logger.info(() -> "Preparing to update concept scheme with IRI=" + toUpdate.getIri());

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(UPDATE_TEMPLATE)) {

            updateStatement.setString(1, toUpdate.getIri());
            updateStatement.setLong(2, toUpdate.getId());
            updateStatement.execute();

            logger.info(() -> "Successfully updated concept with IRI=" + toUpdate.getIri());

            return read(toUpdate.getId());

        } catch (SQLException e) {
            logger.error(() -> e);
            return Optional.empty();
        }
    }

    public boolean delete(long primaryKey) {
        logger.info(() -> "Preparing to delete concept scheme with ID=" + primaryKey);

        try {
            Connection connection = connectionProvider.getConnection();
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TEMPLATE);
            deleteStatement.setLong(1, primaryKey);
            int rowsAffected = deleteStatement.executeUpdate();
            deleteStatement.close();

            logger.info(
                    () ->
                            "Successfully deleted concept scheme with ID="
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
