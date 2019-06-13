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
    void createShouldWriteToTheDb() throws Exception {
        // Given
        ConceptModel concept = buildBaseConcept();

        // When
        underTest.create(concept, connectionProvider.getConnection());

        // Then
        assertEquals(concept, underTest.read(concept.getId()).get());
    }

    @Test
    void createShouldThrowExceptionIfIdIsNotProvided() throws IOException {
        // Given
        ConceptModel concept = buildBaseConcept(null);

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.create(concept, connectionProvider.getConnection()));
    }

    @Test
    void createShouldThrowExceptionIfIdAlreadyExists() throws Exception {
        // Given
        ConceptModel concept = buildBaseConcept();
        underTest.create(concept, connectionProvider.getConnection());

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.create(concept, connectionProvider.getConnection()));
    }

    @Test
    void readShouldRetrieveFromTheDb() throws Exception {
        // Given
        ConceptModel concept = buildBaseConcept();
        underTest.create(concept, connectionProvider.getConnection());

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
    void updateShouldModifyInTheDb() throws Exception {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        underTest.create(toCreate, connectionProvider.getConnection());

        ConceptModel toModify =
                new ConceptModel(
                        toCreate.getId(),
                        fromJsonString(
                                "[{\"language\":\"en\",\"value\":\"one\"},{\"language\":\"fr\",\"value\":\"un\"}]"),
                        toCreate.getAltLabel(),
                        toCreate.getHiddenLabel(),
                        toCreate.getNote(),
                        toCreate.getChangeNote(),
                        toCreate.getEditorialNote(),
                        toCreate.getExample(),
                        toCreate.getHistoryNote(),
                        toCreate.getScopeNote());

        // When
        underTest.update(toModify, connectionProvider.getConnection());

        // Then
        assertEquals(toModify, underTest.read(toCreate.getId()).get());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNotProvided() throws IOException {
        // Given
        ConceptModel toUpdate = buildBaseConcept(null);

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.update(toUpdate, connectionProvider.getConnection()));
    }

    @Test
    void updateShouldThrowExceptionIfIdDoesNotExistInDb() {
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.update(buildBaseConcept(), connectionProvider.getConnection()));
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws Exception {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        underTest.create(toCreate, connectionProvider.getConnection());

        // When
        underTest.delete(toCreate.getId(), connectionProvider.getConnection());

        // Then
        assertEquals(Optional.empty(), underTest.read(toCreate.getId()));
    }

    @Test
    void deleteShouldThrowExceptionIfIdDoesNotExistInDb() {
        String newId = UUID.randomUUID().toString();
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.delete(newId, connectionProvider.getConnection()));
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
