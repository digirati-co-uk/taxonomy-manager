package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ConceptSchemeDaoIntegrationTest {

    private ConnectionProvider connectionProvider;

    private ConceptSchemeDao underTest;

    ConceptSchemeDaoIntegrationTest() {
        this.connectionProvider = new ConnectionProvider();
        this.underTest = new ConceptSchemeDao(connectionProvider);
    }

    @Test
    void createShouldWriteToTheDb() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setId(UUID.randomUUID().toString());

        // When
        ConceptSchemeModel created =
                underTest
                        .create(conceptScheme)
                        .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        // Then
        // TODO how to prove that this actually went via the db?
        assertEquals(conceptScheme.getId(), created.getId());
    }

    @Test
    void readShouldRetrieveFromTheDb() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setId(UUID.randomUUID().toString());
        ConceptSchemeModel created =
                underTest
                        .create(conceptScheme)
                        .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        // When
        ConceptSchemeModel retrieved =
                underTest
                        .read(created.getId())
                        .orElseThrow(
                                () -> new AssertionError("Concept scheme to retrieve not found."));

        // Then
        assertEquals(created, retrieved);
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setId(UUID.randomUUID().toString());
        ConceptSchemeModel created =
                underTest
                        .create(conceptScheme)
                        .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        // When
        boolean deleted = underTest.delete(created.getId());

        // Then
        assertTrue(deleted);
        Optional<ConceptSchemeModel> retrieved = underTest.read(created.getId());
        assertEquals(Optional.empty(), retrieved);
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteAllConceptSchemes =
                        connection.prepareStatement("DELETE FROM concept_scheme")) {

            deleteAllConceptSchemes.execute();

        } catch (SQLException e) {
            fail(e);
        }
    }
}
