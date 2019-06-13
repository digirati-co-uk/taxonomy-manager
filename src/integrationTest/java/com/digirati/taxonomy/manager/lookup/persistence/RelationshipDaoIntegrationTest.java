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
    void createShouldWriteToDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // When
        underTest.create(relationship, connectionProvider.getConnection());

        // Then
        assertEquals(
                relationship,
                underTest.read(relationship.getSourceId(), relationship.getTargetId()).get());
    }

    @Test
    void createShouldThrowExceptionWhenARelationshipAlreadyExistsBetweenSourceAndTarget()
            throws Exception {
        // Given
        underTest.create(
                createBaseModel(SemanticRelationType.RELATED), connectionProvider.getConnection());

        ConceptSemanticRelationModel duplicate = createBaseModel(SemanticRelationType.BROADER);

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.create(duplicate, connectionProvider.getConnection()));
    }

    @Test
    void readShouldRetrieveFromDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        underTest.create(relationship, connectionProvider.getConnection());

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceId(), relationship.getTargetId());

        // Then
        assertEquals(relationship, retrieved.get());
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
    void getRelationshipsShouldReturnAllRelationshipsInvolvingInputId() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        ConceptSemanticRelationModel oneRelatedToTwo =
                new ConceptSemanticRelationModel(
                        UUID.randomUUID().toString(), id, SemanticRelationType.RELATED, false);
        ConceptSemanticRelationModel twoBroaderThanThree =
                new ConceptSemanticRelationModel(
                        id, UUID.randomUUID().toString(), SemanticRelationType.BROADER, false);

        underTest.create(oneRelatedToTwo, connectionProvider.getConnection());
        underTest.create(twoBroaderThanThree, connectionProvider.getConnection());

        // When
        Collection<ConceptSemanticRelationModel> actual = underTest.getRelationships(id);

        // Then
        Collection<ConceptSemanticRelationModel> expected =
                List.of(twoBroaderThanThree, oneRelatedToTwo);
        assertEquals(expected, actual);
    }

    @Test
    void updateShouldModifyInDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        underTest.create(relationship, connectionProvider.getConnection());

        ConceptSemanticRelationModel toModify =
                new ConceptSemanticRelationModel(
                        relationship.getSourceId(),
                        relationship.getTargetId(),
                        SemanticRelationType.BROADER,
                        relationship.isTransitive());

        // When
        underTest.update(toModify, connectionProvider.getConnection());

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
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.update(relationship, connectionProvider.getConnection()));
    }

    @Test
    void deleteShouldRemoveFromDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        underTest.create(relationship, connectionProvider.getConnection());

        // When
        underTest.delete(
                relationship.getSourceId(),
                relationship.getTargetId(),
                connectionProvider.getConnection());

        // Then
        assertEquals(
                Optional.empty(),
                underTest.read(relationship.getSourceId(), relationship.getTargetId()));
    }

    @Test
    void deleteShouldThrowExceptionIfNoSuchRelationshipExists() {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () ->
                        underTest.delete(
                                relationship.getSourceId(),
                                relationship.getTargetId(),
                                connectionProvider.getConnection()));
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
