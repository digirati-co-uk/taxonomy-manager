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

import static org.junit.jupiter.api.Assertions.*;

public class ConceptSchemeDaoIntegrationTest {

    private ConnectionProvider connectionProvider;

    private ConceptSchemeDao underTest;

    ConceptSchemeDaoIntegrationTest() {
        this.connectionProvider = new ConnectionProvider();
        this.underTest = new ConceptSchemeDao(connectionProvider);
    }

    @Test
    void createShouldWriteToTheDb() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");

        // When
        underTest.create(conceptScheme, connectionProvider.getConnection());

        // Then
        assertEquals(conceptScheme, underTest.read(conceptScheme.getId()).get());
    }

    @Test
    void createShouldThrowExceptionIfNoIdIsProvided() {
        ConceptSchemeModel scheme = new ConceptSchemeModel(null, "Example");
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.create(scheme, connectionProvider.getConnection()));
    }

    @Test
    void createShouldThrowExceptionIfIdAlreadyExists() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest.create(conceptScheme, connectionProvider.getConnection());

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.create(conceptScheme, connectionProvider.getConnection()));
    }

    @Test
    void readShouldRetrieveFromTheDb() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest.create(conceptScheme, connectionProvider.getConnection());

        // When
        ConceptSchemeModel retrieved =
                underTest
                        .read(conceptScheme.getId())
                        .orElseThrow(
                                () -> new AssertionError("Concept scheme to retrieve not found."));

        // Then
        assertEquals(conceptScheme, retrieved);
    }

    @Test
    void readShouldReturnEmptyIfIdCannotBeFound() {
        assertEquals(Optional.empty(), underTest.read(UUID.randomUUID().toString()));
    }

    @Test
    void updateShouldModifyInTheDb() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSchemeModel toCreate = new ConceptSchemeModel(id, "Initial Title");
        underTest.create(toCreate, connectionProvider.getConnection());

        ConceptSchemeModel toUpdate = new ConceptSchemeModel(id, "Updated Title");

        // When
        underTest.update(toUpdate, connectionProvider.getConnection());

        // Then
        assertEquals(toUpdate, underTest.read(id).get());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNotPresent() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSchemeModel toCreate = new ConceptSchemeModel(id, "Initial Title");
        underTest.create(toCreate, connectionProvider.getConnection());

        ConceptSchemeModel toUpdate = new ConceptSchemeModel(null, "Updated Title");

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.update(toUpdate, connectionProvider.getConnection()));
    }

    @Test
    void updateShouldThrowExceptionIfConceptIsNotStored() {
        // Given
        ConceptSchemeModel toUpdate =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Updated Title");

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.update(toUpdate, connectionProvider.getConnection()));
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws Exception {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest.create(conceptScheme, connectionProvider.getConnection());

        // When
        underTest.delete(conceptScheme.getId(), connectionProvider.getConnection());

        // Then
        assertEquals(Optional.empty(), underTest.read(conceptScheme.getId()));
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
                PreparedStatement deleteAllConceptSchemes =
                        connection.prepareStatement("DELETE FROM concept_scheme")) {

            deleteAllConceptSchemes.execute();

        } catch (SQLException e) {
            fail(e);
        }
    }
}
