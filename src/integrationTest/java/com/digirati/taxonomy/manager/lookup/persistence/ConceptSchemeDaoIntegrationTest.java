package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

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
    void createShouldWriteToTheDb() {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setIri("http://example.com/conceptScheme#1");

        // When
        ConceptSchemeModel created =
                underTest
                        .create(conceptScheme)
                        .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        // Then
        // TODO how to prove that this actually went via the db?
        assertEquals(conceptScheme.getIri(), created.getIri());
    }

    @Test
    void readShouldRetrieveFromTheDb() {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setIri("http://example.com/conceptScheme#1");
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
    void updateShouldModfifyInTheDb() {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setIri("http://example.com/conceptScheme#1");
        ConceptSchemeModel toModify =
                underTest
                        .create(conceptScheme)
                        .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        toModify.setIri("http://example.com/conceptScheme#2");

        // When
        ConceptSchemeModel updated =
                underTest
                        .update(toModify)
                        .orElseThrow(
                                () -> new AssertionError("Modified concept scheme not found."));

        // Then
        // TODO how to prove that this went via the db?
        assertEquals(toModify, updated);
    }

    @Test
    void deleteShouldRemoveFromTheDb() {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel().setIri("http://example.com/conceptScheme#1");
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
