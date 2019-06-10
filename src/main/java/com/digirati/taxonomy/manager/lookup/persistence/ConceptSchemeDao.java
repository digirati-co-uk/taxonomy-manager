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

public class ConceptSchemeDao {

    private static final Logger logger = LogManager.getLogger(ConceptSchemeDao.class);

    private static final String CREATE_TEMPLATE = "INSERT INTO concept_scheme (id) VALUES (?::UUID)";

    private static final String SELECT_BY_ID_TEMPLATE = "SELECT * FROM concept_scheme WHERE id=?::UUID";

    private static final String DELETE_TEMPLATE = "DELETE FROM concept_scheme WHERE id=?::UUID";

    private final ConnectionProvider connectionProvider;

    ConceptSchemeDao(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public Optional<ConceptSchemeModel> create(ConceptSchemeModel toCreate)
            throws SkosPersistenceException {
        logger.info(() -> "Preparing to create concept scheme with ID=" + toCreate.getId());

        if (toCreate.getId() != null && read(toCreate.getId()).isPresent()) {
            throw SkosPersistenceException.conceptSchemeAlreadyExists(toCreate.getId());
        }

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(CREATE_TEMPLATE)) {

            createStatement.setString(1, toCreate.getId());
            createStatement.execute();

            logger.info(() -> "Successfully created concept with IRI=" + toCreate.getId());

            return read(toCreate.getId());

        } catch (SQLException e) {
            logger.error(() -> e);
            throw SkosPersistenceException.unableToCreateConceptScheme(toCreate.getId(), e);
        }
    }

    private List<ConceptSchemeModel> fromResultSet(ResultSet resultSet) throws SQLException {
        List<ConceptSchemeModel> conceptSchemes = new ArrayList<>();
        while (resultSet.next()) {
            ConceptSchemeModel conceptScheme =
                    new ConceptSchemeModel()
                            .setId(resultSet.getString("id"));
            conceptSchemes.add(conceptScheme);
        }
        return conceptSchemes;
    }

    public Optional<ConceptSchemeModel> read(String id) {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement readStatement =
                        connection.prepareStatement(SELECT_BY_ID_TEMPLATE)) {

            readStatement.setString(1, id);
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

    public boolean delete(String id) throws SkosPersistenceException {
        logger.info(() -> "Preparing to delete concept scheme with ID=" + id);

        if (!read(id).isPresent()) {
            throw SkosPersistenceException.conceptSchemeNotFound(id);
        }

        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(DELETE_TEMPLATE)) {
            deleteStatement.setString(1, id);
            int rowsAffected = deleteStatement.executeUpdate();

            logger.info(
                    () ->
                            "Successfully deleted concept scheme with ID="
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
