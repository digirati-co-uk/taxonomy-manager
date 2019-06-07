package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.RelationshipAlreadyExistsException;
import com.digirati.taxonomy.manager.lookup.exception.RelationshipNotFoundException;
import com.digirati.taxonomy.manager.lookup.exception.UnableToCreateRelationshipException;
import com.digirati.taxonomy.manager.lookup.exception.UnableToUpdateRelationshipException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipDaoIntegrationTest {

    private ConnectionProvider connectionProvider;

    private RelationshipDao underTest;

    RelationshipDaoIntegrationTest() {
        connectionProvider = new ConnectionProvider();
        underTest = new RelationshipDao(connectionProvider);
    }

    @Test
    void createShouldWriteToDb()
            throws RelationshipAlreadyExistsException, RelationshipNotFoundException,
                    UnableToCreateRelationshipException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // When
        ConceptSemanticRelationModel created = underTest.create(relationship);

        // Then
        assertEquals(relationship, created);
    }

    @Test
    void createShouldThrowExceptionWhenARelationshipAlreadyExistsBetweenSourceAndTarget()
            throws RelationshipAlreadyExistsException, RelationshipNotFoundException,
                    UnableToCreateRelationshipException {
        // Given
        underTest.create(createBaseModel(SemanticRelationType.RELATED));

        ConceptSemanticRelationModel duplicate = createBaseModel(SemanticRelationType.BROADER);

        // Then
        assertThrows(RelationshipAlreadyExistsException.class, () -> underTest.create(duplicate));
    }

    @Test
    void readShouldRetrieveFromDb()
            throws RelationshipAlreadyExistsException, RelationshipNotFoundException,
                    UnableToCreateRelationshipException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        ConceptSemanticRelationModel created = underTest.create(relationship);

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceIri(), relationship.getTargetIri());

        // Then
        assertEquals(created, retrieved.get());
    }

    @Test
    void readShouldProvideEmptyResultIfNoSuchRelationshipExists()
            throws RelationshipNotFoundException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.BROADER);

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceIri(), relationship.getTargetIri());

        // Then
        assertEquals(Optional.empty(), retrieved);
    }

    @Test
    void updateShouldModifyInDb()
            throws RelationshipAlreadyExistsException, RelationshipNotFoundException,
                    UnableToCreateRelationshipException, UnableToUpdateRelationshipException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        ConceptSemanticRelationModel created = underTest.create(relationship);

        created.setRelation(SemanticRelationType.BROADER);

        // When
        ConceptSemanticRelationModel modified = underTest.update(created);

        // Then
        assertEquals(created, modified);
    }

    @Test
    void updateShouldThrowExceptionIfNoSuchRelationshipExists() {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // Then
        assertThrows(RelationshipNotFoundException.class, () -> underTest.update(relationship));
    }

    @Test
    void deleteShouldRemoveFromDb()
            throws RelationshipAlreadyExistsException, RelationshipNotFoundException,
                    UnableToCreateRelationshipException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        ConceptSemanticRelationModel created = underTest.create(relationship);

        // When
        boolean deleted = underTest.delete(created.getSourceIri(), created.getTargetIri());

        // Then
        assertTrue(deleted);
        assertEquals(
                Optional.empty(), underTest.read(created.getSourceIri(), created.getTargetIri()));
    }

    @Test
    void deleteShouldThrowExceptionIfNoSuchRelationshipExists() {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // Then
        assertThrows(
                RelationshipNotFoundException.class,
                () -> underTest.delete(relationship.getSourceIri(), relationship.getTargetIri()));
    }

    @AfterEach
    void tearDown() {
        try (Connection connection = connectionProvider.getConnection();
                PreparedStatement deleteAllRelationships =
                        connection.prepareStatement("DELETE FROM concept_semantic_relation")) {

            deleteAllRelationships.executeUpdate();

        } catch (SQLException e) {
            fail(e);
        }
    }

    private ConceptSemanticRelationModel createBaseModel(SemanticRelationType relationType) {
        return new ConceptSemanticRelationModel()
                .setRelation(relationType)
                .setTransitive(false)
                .setSourceId(1L)
                .setTargetId(2L)
                .setSourceIri("http://example.com/concept#1")
                .setTargetIri("http://example.com/concept#2");
    }
}
