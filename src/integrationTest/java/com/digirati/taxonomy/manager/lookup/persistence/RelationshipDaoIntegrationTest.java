package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RelationshipDaoIntegrationTest {

    private Connection connection;

    private RelationshipDao underTest;

    RelationshipDaoIntegrationTest() {
        underTest = new RelationshipDao();
    }

    @BeforeEach
    void setup() throws SQLException {
        connection = new ConnectionProvider().getConnection();
        connection.setAutoCommit(false);
    }

    @Test
    void createShouldWriteToDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // When
        underTest.create(relationship, connection);

        // Then
        assertEquals(
                relationship,
                underTest
                        .read(relationship.getSourceId(), relationship.getTargetId(), connection)
                        .get());
    }

    @Test
    void createShouldThrowExceptionWhenARelationshipAlreadyExistsBetweenSourceAndTarget()
            throws Exception {
        // Given
        underTest.create(createBaseModel(SemanticRelationType.RELATED), connection);

        ConceptSemanticRelationModel duplicate = createBaseModel(SemanticRelationType.BROADER);

        // Then
        assertThrows(SkosPersistenceException.class, () -> underTest.create(duplicate, connection));
    }

    @Test
    void readShouldRetrieveFromDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        underTest.create(relationship, connection);

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceId(), relationship.getTargetId(), connection);

        // Then
        assertEquals(relationship, retrieved.get());
    }

    @Test
    void readShouldProvideEmptyResultIfNoSuchRelationshipExists() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.BROADER);

        // When
        Optional<ConceptSemanticRelationModel> retrieved =
                underTest.read(relationship.getSourceId(), relationship.getTargetId(), connection);

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

        underTest.create(oneRelatedToTwo, connection);
        underTest.create(twoBroaderThanThree, connection);

        // When
        Collection<ConceptSemanticRelationModel> actual =
                underTest.getRelationships(id, connection);

        // Then
        Collection<ConceptSemanticRelationModel> expected =
                List.of(twoBroaderThanThree, oneRelatedToTwo);
        assertEquals(expected, actual);
    }

    @Test
    void updateShouldModifyInDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        underTest.create(relationship, connection);

        ConceptSemanticRelationModel toModify =
                new ConceptSemanticRelationModel(
                        relationship.getSourceId(),
                        relationship.getTargetId(),
                        SemanticRelationType.BROADER,
                        relationship.isTransitive());

        // When
        underTest.update(toModify, connection);

        // Then
        assertEquals(
                toModify,
                underTest
                        .read(relationship.getSourceId(), relationship.getTargetId(), connection)
                        .get());
    }

    @Test
    void updateShouldThrowExceptionIfNoSuchRelationshipExists() {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);

        // Then
        assertThrows(
                SkosPersistenceException.class, () -> underTest.update(relationship, connection));
    }

    @Test
    void deleteShouldRemoveFromDb() throws Exception {
        // Given
        ConceptSemanticRelationModel relationship = createBaseModel(SemanticRelationType.RELATED);
        underTest.create(relationship, connection);

        // When
        underTest.delete(relationship.getSourceId(), relationship.getTargetId(), connection);

        // Then
        assertEquals(
                Optional.empty(),
                underTest.read(relationship.getSourceId(), relationship.getTargetId(), connection));
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
                                connection));
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
        connection.close();
    }

    private ConceptSemanticRelationModel createBaseModel(SemanticRelationType relationType) {
        return new ConceptSemanticRelationModel(
                "1c3f8ea2-73e2-4ea7-ae00-207ac1513920",
                "88a15250-2d59-41f2-bbef-5b9239e31b25",
                relationType,
                false);
    }
}
