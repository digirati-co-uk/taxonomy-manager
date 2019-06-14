package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConceptDaoIntegrationTest {

    private Connection connection;

    private ConceptDao underTest;

    ConceptDaoIntegrationTest() {
        this.underTest = new ConceptDao();
    }

    @BeforeEach
    void setup() throws SQLException {
        connection = new ConnectionProvider().getConnection();
        connection.setAutoCommit(false);
    }

    @Test
    void createShouldWriteToTheDb() throws Exception {
        // Given
        ConceptModel concept = buildBaseConcept();

        // When
        underTest.create(concept, connection);

        // Then
        assertEquals(concept, underTest.read(concept.getId(), connection).get());
    }

    @Test
    void createShouldThrowExceptionIfIdIsNotProvided() throws IOException {
        // Given
        ConceptModel concept = buildBaseConcept(null);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(concept, connection));
    }

    @Test
    void createShouldThrowExceptionIfIdAlreadyExists() throws Exception {
        // Given
        ConceptModel concept = buildBaseConcept();
        underTest.create(concept, connection);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(concept, connection));
    }

    @Test
    void readShouldRetrieveFromTheDb() throws Exception {
        // Given
        ConceptModel concept = buildBaseConcept();
        underTest.create(concept, connection);

        // When
        ConceptModel retrieved =
                underTest
                        .read(concept.getId(), connection)
                        .orElseThrow(() -> new AssertionError("Concept to retrieve not found."));

        // Then
        assertEquals(concept, retrieved);
    }

    @Test
    void readShouldReturnEmptyIfIdDoesNotExist() throws SkosPersistenceException {
        assertEquals(Optional.empty(), underTest.read(UUID.randomUUID().toString(), connection));
    }

    @Test
    void updateShouldModifyInTheDb() throws Exception {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        underTest.create(toCreate, connection);

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
        underTest.update(toModify, connection);

        // Then
        assertEquals(toModify, underTest.read(toCreate.getId(), connection).get());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNotProvided() throws IOException {
        // Given
        ConceptModel toUpdate = buildBaseConcept(null);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(toUpdate, connection));
    }

    @Test
    void updateShouldThrowExceptionIfIdDoesNotExistInDb() {
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.update(buildBaseConcept(), connection));
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws Exception {
        // Given
        ConceptModel toCreate = buildBaseConcept();
        underTest.create(toCreate, connection);

        // When
        underTest.delete(toCreate.getId(), connection);

        // Then
        assertEquals(Optional.empty(), underTest.read(toCreate.getId(), connection));
    }

    @Test
    void deleteShouldThrowExceptionIfIdDoesNotExistInDb() {
        String newId = UUID.randomUUID().toString();
        assertThrows(SkosPersistenceException.class, () -> underTest.delete(newId, connection));
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
        connection.close();
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
