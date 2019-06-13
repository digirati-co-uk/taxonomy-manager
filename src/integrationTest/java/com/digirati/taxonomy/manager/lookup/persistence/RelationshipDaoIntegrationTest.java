package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipDaoIntegrationTest {

    private ConnectionProvider connectionProvider;

    private RelationshipDao underTest;

    RelationshipDaoIntegrationTest() {
        connectionProvider = new ConnectionProvider();
        underTest = new RelationshipDao(connectionProvider);
    }

    @Test
    void createShouldWriteToDb() throws SkosPersistenceException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // When
        underTest.create(relationship);

        // Then
        assertEquals(
                relationship,
                underTest.read(relationship.getSourceId(), relationship.getTargetId()).get());
    }

    @Test
    void createShouldThrowExceptionWhenARelationshipAlreadyExistsBetweenSourceAndTarget()
            throws SkosPersistenceException {
        // Given
        underTest.create(createBaseModel(SemanticRelationType.RELATED));

        ConceptSemanticRelationModel duplicate = createBaseModel(SemanticRelationType.BROADER);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(duplicate));
    }

    @Test
    void readShouldRetrieveFromDb() throws SkosPersistenceException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        ConceptSemanticRelationModel created = underTest.create(relationship);

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceId(), relationship.getTargetId());

        // Then
        assertEquals(created, retrieved.get());
    }

    @Test
    void readShouldProvideEmptyResultIfNoSuchRelationshipExists() throws SkosPersistenceException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.BROADER);

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceId(), relationship.getTargetId());

        // Then
        assertEquals(Optional.empty(), retrieved);
    }

    @Test
    void getRelationshipsShouldReturnAllRelationshipsInvolvingInputId()
            throws SkosPersistenceException {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSemanticRelationModel oneRelatedToTwo =
                new ConceptSemanticRelationModel(
                        UUID.randomUUID().toString(), id, SemanticRelationType.RELATED, false);
        ConceptSemanticRelationModel twoBroaderThanThree =
                new ConceptSemanticRelationModel(
                        id, UUID.randomUUID().toString(), SemanticRelationType.BROADER, false);

        underTest.create(oneRelatedToTwo);
        underTest.create(twoBroaderThanThree);

        // When
        Collection<ConceptSemanticRelationModel> actual = underTest.getRelationships(id);

        // Then
        Collection<ConceptSemanticRelationModel> expected =
                List.of(twoBroaderThanThree, oneRelatedToTwo);
        assertEquals(expected, actual);
    }

    @Test
    void updateShouldModifyInDb() throws SkosPersistenceException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        ConceptSemanticRelationModel created = underTest.create(relationship);

        ConceptSemanticRelationModel toModify =
                new ConceptSemanticRelationModel(
                        created.getSourceId(),
                        created.getTargetId(),
                        SemanticRelationType.BROADER,
                        created.isTransitive());

        // When
        underTest.update(toModify);

        // Then
        assertEquals(
                toModify,
                underTest.read(relationship.getSourceId(), relationship.getTargetId()).get());
    }

    @Test
    void updateShouldThrowExceptionIfNoSuchRelationshipExists() {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.update(relationship));
    }

    @Test
    void deleteShouldRemoveFromDb() throws SkosPersistenceException {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        ConceptSemanticRelationModel created = underTest.create(relationship);

        // When
        boolean deleted = underTest.delete(created.getSourceId(), created.getTargetId());

        // Then
        assertTrue(deleted);
        assertEquals(
                Optional.empty(), underTest.read(created.getSourceId(), created.getTargetId()));
    }

    @Test
    void deleteShouldThrowExceptionIfNoSuchRelationshipExists() {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.delete(relationship.getSourceId(), relationship.getTargetId()));
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
        return new ConceptSemanticRelationModel(
                "1c3f8ea2-73e2-4ea7-ae00-207ac1513920",
                "88a15250-2d59-41f2-bbef-5b9239e31b25",
                relationType,
                false);
    }
}
