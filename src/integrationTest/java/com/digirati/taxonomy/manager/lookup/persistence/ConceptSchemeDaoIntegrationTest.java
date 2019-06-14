package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConceptSchemeDaoIntegrationTest {

    private Connection connection;

    private ConceptSchemeDao underTest;

    ConceptSchemeDaoIntegrationTest() {
        this.underTest = new ConceptSchemeDao();
    }

    @BeforeEach
    void setup() throws SQLException {
        connection = new ConnectionProvider().getConnection();
        connection.setAutoCommit(false);
    }

    @Test
    void createShouldWriteToTheDb() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");

        // When
        underTest.create(conceptScheme, connection);

        // Then
        assertEquals(conceptScheme, underTest.read(conceptScheme.getId(), connection).get());
    }

    @Test
    void createShouldThrowExceptionIfNoIdIsProvided() {
        ConceptSchemeModel scheme = new ConceptSchemeModel(null, "Example");
        assertThrows(SkosPersistenceException.class, () -> underTest.create(scheme, connection));
    }

    @Test
    void createShouldThrowExceptionIfIdAlreadyExists() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest.create(conceptScheme, connection);

        // Then
        assertThrows(
                SkosPersistenceException.class, () -> underTest.create(conceptScheme, connection));
    }

    @Test
    void readShouldRetrieveFromTheDb() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest.create(conceptScheme, connection);

        // When
        ConceptSchemeModel retrieved =
                underTest
                        .read(conceptScheme.getId(), connection)
                        .orElseThrow(
                                () -> new AssertionError("Concept scheme to retrieve not found."));

        // Then
        assertEquals(conceptScheme, retrieved);
    }

    @Test
    void readShouldReturnEmptyIfIdCannotBeFound() throws SkosPersistenceException {
        assertEquals(Optional.empty(), underTest.read(UUID.randomUUID().toString(), connection));
    }

    @Test
    void updateShouldModifyInTheDb() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSchemeModel toCreate = new ConceptSchemeModel(id, "Initial Title");
        underTest.create(toCreate, connection);

        ConceptSchemeModel toUpdate = new ConceptSchemeModel(id, "Updated Title");

        // When
        underTest.update(toUpdate, connection);

        // Then
        assertEquals(toUpdate, underTest.read(id, connection).get());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNotPresent() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSchemeModel toCreate = new ConceptSchemeModel(id, "Initial Title");
        underTest.create(toCreate, connection);

        ConceptSchemeModel toUpdate = new ConceptSchemeModel(null, "Updated Title");

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(toUpdate, connection));
    }

    @Test
    void updateShouldThrowExceptionIfConceptIsNotStored() {
        // Given
        ConceptSchemeModel toUpdate =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Updated Title");

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(toUpdate, connection));
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest.create(conceptScheme, connection);

        // When
        underTest.delete(conceptScheme.getId(), connection);

        // Then
        assertEquals(Optional.empty(), underTest.read(conceptScheme.getId(), connection));
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
}
