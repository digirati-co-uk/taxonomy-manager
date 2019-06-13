package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConceptDaoIntegrationTest {

    private ConnectionProvider connectionProvider;

    private ConceptDao underTest;

    ConceptDaoIntegrationTest() {
        this.connectionProvider = new ConnectionProvider();
        this.underTest = new ConceptDao(connectionProvider);
    }

    @Test
    void createShouldWriteToTheDb() throws SkosPersistenceException, IOException {
        // Given
        ConceptModel concept = buildBaseConcept();

        // When
        underTest
                .create(concept)
                .orElseThrow(() -> new AssertionError("Created concept not found."));

        // Then
        assertEquals(concept, underTest.read(concept.getId()).get());
    }

    @Test
    void createShouldThrowExceptionIfIdIsNotProvided() throws IOException {
        // Given
        ConceptModel concept = buildBaseConcept(null);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(concept));
    }

    @Test
    void createShouldThrowExceptionIfIdAlreadyExists()
            throws IOException, SkosPersistenceException {
        // Given
        ConceptModel concept = buildBaseConcept();
        underTest
                .create(concept)
                .orElseThrow(() -> new AssertionError("Created concept not found."));

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(concept));
    }

    @Test
    void readShouldRetrieveFromTheDb() throws SkosPersistenceException, IOException {
        // Given
        ConceptModel concept = buildBaseConcept();
        underTest
                .create(concept)
                .orElseThrow(() -> new AssertionError("Created concept not found."));

        // When
        ConceptModel retrieved =
                underTest
                        .read(concept.getId())
                        .orElseThrow(() -> new AssertionError("Concept to retrieve not found."));

        // Then
        assertEquals(concept, retrieved);
    }

    @Test
    void readShouldReturnEmptyIfIdDoesNotExist() {
        assertEquals(Optional.empty(), underTest.read(UUID.randomUUID().toString()));
    }

    @Test
    void updateShouldModifyInTheDb() throws SkosPersistenceException, IOException {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        ConceptModel created =
                underTest
                        .create(toCreate)
                        .orElseThrow(() -> new AssertionError("Created concept not found."));

        ConceptModel toModify =
                new ConceptModel(
                        created.getId(),
                        fromJsonString(
                                "[{\"language\":\"en\",\"value\":\"one\"},{\"language\":\"fr\",\"value\":\"un\"}]"),
                        created.getAltLabel(),
                        created.getHiddenLabel(),
                        created.getNote(),
                        created.getChangeNote(),
                        created.getEditorialNote(),
                        created.getExample(),
                        created.getHistoryNote(),
                        created.getScopeNote());

        // When
        underTest
                .update(toModify)
                .orElseThrow(() -> new AssertionError("Modified concept not found."));

        // Then
        assertEquals(toModify, underTest.read(created.getId()).get());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNotProvided() throws IOException {
        // Given
        ConceptModel toUpdate = buildBaseConcept(null);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(toUpdate));
    }

    @Test
    void updateShouldThrowExceptionIfIdDoesNotExistInDb() {
        assertThrows(SkosPersistenceException.class, () -> underTest.update(buildBaseConcept()));
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws SkosPersistenceException, IOException {
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

    @Test
    void deleteShouldThrowExceptionIfIdDoesNotExistInDb() {
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.delete(UUID.randomUUID().toString()));
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

    private ConceptModel buildBaseConcept() throws IOException {
        return buildBaseConcept(UUID.randomUUID().toString());
    }

    private ConceptModel buildBaseConcept(String id) throws IOException {
        return new ConceptModel(
                id,
                fromJsonString("[{\"language\":\"en\",\"value\":\"one\"}]"),
                fromJsonString("[{\"language\":\"en\",\"value\":\"first\"}]"),
                fromJsonString("[{\"language\":\"en\",\"value\":\"1\"}]"),
                fromJsonString("[{\"language\":\"en\",\"value\":\"the first number\"}]"),
                fromJsonString(
                        "[{\"language\":\"en\",\"value\":\"First introduced ~14 billion years ago.\"}]"),
                fromJsonString(
                        "[{\"language\":\"en\",\"value\":\"This is a pretty handy number to have.\"}]"),
                fromJsonString(
                        "[{\"language\":\"en\",\"value\":\"The string '1' contains exactly 1 character.\"}]"),
                fromJsonString(
                        "[{\"language\":\"en\",\"value\":\"First introduced ~14 billion years ago. Hasn't changed since.\"}]"),
                fromJsonString(
                        "[{\"language\":\"en\",\"value\":\"Used as the basis against which to define all other numbers.\"}]"));
    }

    private JsonNode fromJsonString(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }
}
