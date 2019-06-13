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
    void createShouldWriteToTheDb() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");

        // When
        underTest
                .create(conceptScheme)
                .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        // Then
        assertEquals(conceptScheme, underTest.read(conceptScheme.getId()).get());
    }

    @Test
    void createShouldThrowExceptionIfNoIdIsProvided() {
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.create(new ConceptSchemeModel(null, "Example")));
    }

    @Test
    void createShouldThrowExceptionIfIdAlreadyExists() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
        underTest
                .create(conceptScheme)
                .orElseThrow(() -> new AssertionError("Created concept scheme not found."));

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(conceptScheme));
    }

    @Test
    void readShouldRetrieveFromTheDb() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
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
    void readShouldReturnEmptyIfIdCannotBeFound() {
        assertEquals(Optional.empty(), underTest.read(UUID.randomUUID().toString()));
    }

    @Test
    void updateShouldModifyInTheDb() throws SkosPersistenceException {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSchemeModel toCreate = new ConceptSchemeModel(id, "Initial Title");
        underTest.create(toCreate);

        ConceptSchemeModel toUpdate = new ConceptSchemeModel(id, "Updated Title");

        // When
        underTest.update(toUpdate);

        // Then
        assertEquals(toUpdate, underTest.read(id).get());
    }

    @Test
    void updateShouldThrowExceptionIfIdIsNotPresent() throws SkosPersistenceException {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSchemeModel toCreate = new ConceptSchemeModel(id, "Initial Title");
        underTest.create(toCreate);

        ConceptSchemeModel toUpdate = new ConceptSchemeModel(null, "Updated Title");

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(toUpdate));
    }

    @Test
    void updateShouldThrowExceptionIfConceptIsNotStored() {
        // Given
        ConceptSchemeModel toUpdate =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Updated Title");

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(toUpdate));
    }

    @Test
    void deleteShouldRemoveFromTheDb() throws SkosPersistenceException {
        // Given
        ConceptSchemeModel conceptScheme =
                new ConceptSchemeModel(UUID.randomUUID().toString(), "Example Scheme");
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

    @Test
    void deleteShouldThrowExceptionIfIdDoesNotExistInDb() {
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.delete(UUID.randomUUID().toString()));
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
