package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConceptDaoIntegrationTest {

    private ConnectionProvider connectionProvider;

    private ConceptDao underTest;

    ConceptDaoIntegrationTest() {
        this.connectionProvider = new ConnectionProvider();
        this.underTest = new ConceptDao(connectionProvider);
    }

    @Test
    void createShouldWriteToTheDb() {
        // Given
        ConceptModel concept = buildBaseConcept();

        // When
        ConceptModel created =
                underTest
                        .create(concept)
                        .orElseThrow(() -> new AssertionError("Created concept not found."));

        // Then
        // TODO how to prove that this actually went via the db?
        assertEquals(concept.getIri(), created.getIri());
        // TODO jsonb field order is getting swapped around
        // assertEquals(concept.getPreferredLabel(), created.getPreferredLabel());
        // assertEquals(concept.getAltLabel(), created.getAltLabel());
        // assertEquals(concept.getHiddenLabel(), created.getHiddenLabel());
        // assertEquals(concept.getNote(), created.getNote());
        // assertEquals(concept.getChangeNote(), created.getChangeNote());
        // assertEquals(concept.getEditorialNote(), created.getEditorialNote());
        // assertEquals(concept.getExample(), created.getExample());
        // assertEquals(concept.getHistoryNote(), created.getHistoryNote());
        // assertEquals(concept.getScopeNote(), created.getScopeNote());
    }

    @Test
    void readShouldRetrieveFromTheDb() {
        // Given
        ConceptModel concept = buildBaseConcept();
        ConceptModel created =
                underTest
                        .create(concept)
                        .orElseThrow(() -> new AssertionError("Created concept not found."));

        // When
        ConceptModel retrieved =
                underTest
                        .read(created.getId())
                        .orElseThrow(() -> new AssertionError("Concept to retrieve not found."));

        // Then
        assertEquals(created, retrieved);
    }

    @Test
    void updateShouldModifyInTheDb() {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        ConceptModel toModify =
                underTest
                        .create(toCreate)
                        .orElseThrow(() -> new AssertionError("Created concept not found."));

        toModify.setPreferredLabel(
                "[{\"language\":\"en\",\"value\":\"one\"},{\"language\":\"fr\",\"value\":\"un\"}]");

        // When
        ConceptModel updated =
                underTest
                        .update(toModify)
                        .orElseThrow(() -> new AssertionError("Modified concept not found."));

        // Then
        // TODO this really doesn't prove anything because update could just return back its arg
        assertEquals(toModify.getId(), updated.getId());
        assertEquals(toModify.getIri(), updated.getIri());
        // TODO json fields getting swapped around
        // assertEquals(toModify, updated);
    }

    @Test
    void deleteShouldRemoveFromTheDb() {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        ConceptModel created =
                underTest
                        .create(toCreate)
                        .orElseThrow(() -> new AssertionError("Created concept not found."));

        // When
        boolean deleted = underTest.delete(created.getId());

        // Then
        Assertions.assertTrue(deleted);
        Optional<ConceptModel> retrieved = underTest.read(created.getId());
        assertEquals(Optional.empty(), retrieved);
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteAllConcepts =
                        connection.prepareStatement("DELETE FROM concept")) {

            deleteAllConcepts.executeUpdate();

        } catch (SQLException e) {
            Assertions.fail(e);
        }
    }

    private ConceptModel buildBaseConcept() {
        return new ConceptModel()
                .setIri("http://example.com/concept#1")
                .setPreferredLabel("[{\"language\":\"en\",\"value\":\"one\"}]")
                .setAltLabel("[{\"language\":\"en\",\"value\":\"first\"}]")
                .setHiddenLabel("[{\"language\":\"en\",\"value\":\"1\"}]")
                .setNote("[{\"language\":\"en\",\"value\":\"the first number\"}]")
                .setChangeNote(
                        "[{\"language\":\"en\",\"value\":\"First introduced ~14 billion years ago.\"}]")
                .setEditorialNote(
                        "[{\"language\":\"en\",\"value\":\"This is a pretty handy number to have.\"}]")
                .setExample(
                        "[{\"language\":\"en\",\"value\":\"The string '1' contains exactly 1 character.\"}]")
                .setHistoryNote(
                        "[{\"language\":\"en\",\"value\":\"First introduced ~14 billion years ago. Hasn't changed since.\"}]")
                .setScopeNote(
                        "[{\"language\":\"en\",\"value\":\"Used as the basis against which to define all other numbers.\"}]");
    }
}
