package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SkosPersistenceServiceTest {

    private static final ConceptSchemeModel conceptScheme = initialiseConceptScheme();
    private static final ConceptModel conceptOne = initialiseConcept("1", "one", "un");
    private static final ConceptModel conceptTwo = initialiseConcept("2", "two", "deux");
    private static final ConceptModel conceptThree = initialiseConcept("3", "three", "trois");
    private static final ConceptSemanticRelationModel schemeHasTopConceptOne =
            initialiseRelationship(
                    "http://example.com/",
                    "http://example.com/concept#1",
                    SemanticRelationType.HAS_TOP_CONCEPT);
    private static final ConceptSemanticRelationModel schemeHasTopConceptTwo =
            initialiseRelationship(
                    "http://example.com/",
                    "http://example.com/concept#2",
                    SemanticRelationType.HAS_TOP_CONCEPT);
    private static final ConceptSemanticRelationModel threeIsRelatedToTwo =
            initialiseRelationship(
                    "http://example.com/concept#3",
                    "http://example.com/concept#2",
                    SemanticRelationType.RELATED);

    @Mock private SkosTranslator skosTranslator;

    @Mock private ConceptDao conceptDao;

    @Mock private ConceptSchemeDao conceptSchemeDao;

    @Mock private RelationshipDao relationshipDao;

    private SkosPersistenceService underTest;

    @BeforeEach
    void setup() {
        underTest =
                new SkosPersistenceService(
                        conceptDao, conceptSchemeDao, relationshipDao, skosTranslator);
    }

    @Test
    void createShouldPersistEachEntityFromSkosInput() throws SkosPersistenceException {
        // Given
        RdfModel rdfModel =
                new RdfModel(
                        List.of(conceptOne, conceptTwo, conceptThree),
                        Collections.singletonList(conceptScheme),
                        List.of(
                                schemeHasTopConceptOne,
                                schemeHasTopConceptTwo,
                                threeIsRelatedToTwo));
        InputStream skosInput = givenSkosInput(rdfModel);

        // When
        underTest.create(skosInput, "http://example.com/", SkosFileType.JSON_LD);

        // Then
        Map<String, String> originalIdsToGeneratedIds = new HashMap<>();
        ArgumentCaptor<ConceptModel> conceptCaptor = ArgumentCaptor.forClass(ConceptModel.class);
        verify(conceptDao, times(rdfModel.getConcepts().size())).create(conceptCaptor.capture());
        List<ConceptModel> persistedConcepts = conceptCaptor.getAllValues();
        for (int i = 0; i < persistedConcepts.size(); i++) {
            ConceptModel actual = persistedConcepts.get(i);
            ConceptModel expected = ((List<ConceptModel>) rdfModel.getConcepts()).get(i);
            originalIdsToGeneratedIds.put(expected.getId(), actual.getId());
            assertEquals(expected.getPreferredLabel(), actual.getPreferredLabel());
            assertEquals(expected.getAltLabel(), actual.getAltLabel());
            assertEquals(expected.getHiddenLabel(), actual.getHiddenLabel());
            assertEquals(expected.getNote(), actual.getNote());
            assertEquals(expected.getChangeNote(), actual.getChangeNote());
            assertEquals(expected.getEditorialNote(), actual.getEditorialNote());
            assertEquals(expected.getExample(), actual.getExample());
            assertEquals(expected.getHistoryNote(), actual.getHistoryNote());
            assertEquals(expected.getScopeNote(), actual.getScopeNote());
        }

        ArgumentCaptor<ConceptSchemeModel> conceptSchemeCaptor =
                ArgumentCaptor.forClass(ConceptSchemeModel.class);
        verify(conceptSchemeDao, times(rdfModel.getConceptSchemes().size()))
                .create(conceptSchemeCaptor.capture());
        List<ConceptSchemeModel> persistedSchemes = conceptSchemeCaptor.getAllValues();
        for (int i = 0; i < persistedSchemes.size(); i++) {
            ConceptSchemeModel actual = persistedSchemes.get(i);
            ConceptSchemeModel expected =
                    ((List<ConceptSchemeModel>) rdfModel.getConceptSchemes()).get(i);
            originalIdsToGeneratedIds.put(expected.getId(), actual.getId());
            assertEquals(expected.getTitle(), actual.getTitle());
        }

        ArgumentCaptor<ConceptSemanticRelationModel> relationshipCaptor =
                ArgumentCaptor.forClass(ConceptSemanticRelationModel.class);
        verify(relationshipDao, times(rdfModel.getRelationships().size()))
                .create(relationshipCaptor.capture());
        List<ConceptSemanticRelationModel> persistedRelationships =
                relationshipCaptor.getAllValues();
        for (int i = 0; i < persistedRelationships.size(); i++) {
            ConceptSemanticRelationModel actual = persistedRelationships.get(i);
            ConceptSemanticRelationModel expected =
                    ((List<ConceptSemanticRelationModel>) rdfModel.getRelationships()).get(i);
            assertEquals(
                    originalIdsToGeneratedIds.get(expected.getSourceId()), actual.getSourceId());
            assertEquals(
                    originalIdsToGeneratedIds.get(expected.getTargetId()), actual.getTargetId());
            assertEquals(expected.getRelation(), actual.getRelation());
            assertEquals(expected.isTransitive(), actual.isTransitive());
        }
    }

    @Test
    void getConceptShouldRetrieveConceptAndRelatedEntities() throws SkosPersistenceException {
        // Given
        givenEntitiesRelatedToConceptTwoExist();
        RdfModel relatedToConceptTwo =
                new RdfModel(
                        Sets.newHashSet(conceptTwo, conceptThree),
                        Sets.newHashSet(conceptScheme),
                        Sets.newHashSet(schemeHasTopConceptTwo, threeIsRelatedToTwo));
        Model expectedSkosOutput = givenSkosOutput(relatedToConceptTwo);

        // When
        underTest.getConcept(conceptTwo.getId(), SkosFileType.JSON_LD);

        // Then
        verify(expectedSkosOutput)
                .write(
                        any(ByteArrayOutputStream.class),
                        eq(SkosFileType.JSON_LD.getFileTypeName()));
    }

    @Test
    void getConceptShouldThrowExceptionIfConceptCannotBeFound() {
        // Given
        given(conceptDao.read(conceptTwo.getId())).willReturn(Optional.empty());

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.getConcept(conceptTwo.getId(), SkosFileType.JSON_LD));
    }

    @Test
    void getConceptShouldThrowExceptionIfRelatedEntityCannotBeFound()
            throws SkosPersistenceException {
        // Given
        given(conceptDao.read(conceptTwo.getId())).willReturn(Optional.of(conceptTwo));
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.empty());
        given(conceptDao.read(conceptScheme.getId())).willReturn(Optional.empty());
        given(relationshipDao.getRelationships(conceptTwo.getId()))
                .willReturn(Sets.newHashSet(schemeHasTopConceptTwo));

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.getConcept(conceptTwo.getId(), SkosFileType.JSON_LD));
    }

    @Test
    void getConceptSchemeShouldRetrieveConceptAndRelatedEntities() throws SkosPersistenceException {
        // Given
        givenEntitiesRelatedToConceptSchemeExist();
        RdfModel relatedToConceptScheme =
                new RdfModel(
                        Sets.newHashSet(conceptOne, conceptTwo),
                        Sets.newHashSet(conceptScheme),
                        Sets.newHashSet(schemeHasTopConceptOne, schemeHasTopConceptTwo));
        Model expectedSkosOutput = givenSkosOutput(relatedToConceptScheme);

        // When
        underTest.getConceptScheme(conceptScheme.getId(), SkosFileType.JSON_LD);

        // Then
        verify(expectedSkosOutput)
                .write(
                        any(ByteArrayOutputStream.class),
                        eq(SkosFileType.JSON_LD.getFileTypeName()));
    }

    @Test
    void getConceptSchemeShouldThrowExceptionIfConceptCannotBeFound() {
        // Given
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.empty());

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.getConceptScheme(conceptScheme.getId(), SkosFileType.JSON_LD));
    }

    @Test
    void getConceptSchemeShouldThrowExceptionIfRelatedEntityCannotBeFound()
            throws SkosPersistenceException {
        // Given
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.of(conceptScheme));
        given(conceptDao.read(conceptOne.getId())).willReturn(Optional.empty());
        given(conceptSchemeDao.read(conceptOne.getId())).willReturn(Optional.empty());
        given(relationshipDao.getRelationships(conceptScheme.getId()))
                .willReturn(Sets.newHashSet(schemeHasTopConceptOne));

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.getConceptScheme(conceptScheme.getId(), SkosFileType.JSON_LD));
    }

    @Test
    void updateShouldPersistEachEntityFromSkosInput() throws SkosPersistenceException {
        // Given
        RdfModel rdfModel =
                new RdfModel(
                        Arrays.asList(conceptOne, conceptTwo, conceptThree),
                        Collections.singletonList(conceptScheme),
                        Arrays.asList(
                                schemeHasTopConceptOne,
                                schemeHasTopConceptTwo,
                                threeIsRelatedToTwo));
        InputStream skosInput = givenSkosInput(rdfModel);

        // When
        underTest.update(skosInput, "http://example.com/", SkosFileType.JSON_LD);

        // Then
        for (ConceptModel concept : rdfModel.getConcepts()) {
            verify(conceptDao).update(concept);
        }
        for (ConceptSchemeModel conceptScheme : rdfModel.getConceptSchemes()) {
            verify(conceptSchemeDao).update(conceptScheme);
        }
        for (ConceptSemanticRelationModel relationship : rdfModel.getRelationships()) {
            verify(relationshipDao).update(relationship);
        }
    }

    @Test
    void deleteConceptSchemeShouldDeleteSchemeAndRelationships() throws SkosPersistenceException {
        // Given
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.of(conceptScheme));

        // When
        underTest.deleteConceptScheme(conceptScheme.getId());

        // Then
        verify(relationshipDao).delete(conceptScheme.getId());
        verify(conceptSchemeDao).delete(conceptScheme.getId());
    }

    @Test
    void deleteConceptSchemeShouldThrowExceptionIfSchemeDoesNotExist() {
        // Given
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.empty());

        // Then
        assertThrows(
                SkosPersistenceException.class,
                () -> underTest.deleteConceptScheme(conceptScheme.getId()));
    }

    @Test
    void deleteConceptShouldDeleteConceptAndRelationships() throws SkosPersistenceException {
        // Given
        given(conceptDao.read(conceptOne.getId())).willReturn(Optional.of(conceptOne));

        // When
        underTest.deleteConcept(conceptOne.getId());

        // Then
        verify(relationshipDao).delete(conceptOne.getId());
        verify(conceptDao).delete(conceptOne.getId());
    }

    @Test
    void deleteConceptShouldThrowExceptionIfConceptDoesNotExist() {
        // Given
        given(conceptDao.read(conceptOne.getId())).willReturn(Optional.empty());

        // Then
        assertThrows(
                SkosPersistenceException.class, () -> underTest.deleteConcept(conceptOne.getId()));
    }

    private static ConceptSchemeModel initialiseConceptScheme() {
        return new ConceptSchemeModel("http://example.com/", "Example Scheme");
    }

    private static ConceptModel initialiseConcept(
            String id, String englishLabel, String frenchLabel) {
        String preferredLabelString =
                "[{\"language\":\"fr\",\"value\":\""
                        + frenchLabel
                        + "\"},{\"language\":\"en\",\"value\":\""
                        + englishLabel
                        + "\"}]";

        JsonNode preferredLabel = null;
        try {
            preferredLabel = new ObjectMapper().readTree(preferredLabelString);
        } catch (IOException e) {
            fail(e);
        }

        return new ConceptModel(
                "http://example.com/concept#" + id,
                preferredLabel,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private static ConceptSemanticRelationModel initialiseRelationship(
            String sourceId, String targetId, SemanticRelationType relationType) {
        return new ConceptSemanticRelationModel(sourceId, targetId, relationType, false);
    }

    private InputStream givenSkosInput(RdfModel rdfModel) {
        InputStream i = mock(InputStream.class);
        given(skosTranslator.translate(eq(i), eq("http://example.com/"), eq(SkosFileType.JSON_LD)))
                .willReturn(rdfModel);
        return i;
    }

    private void givenEntitiesRelatedToConceptTwoExist() throws SkosPersistenceException {
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.of(conceptScheme));
        given(conceptDao.read(conceptScheme.getId())).willReturn(Optional.empty());
        given(conceptDao.read(conceptTwo.getId())).willReturn(Optional.of(conceptTwo));
        given(conceptDao.read(conceptThree.getId())).willReturn(Optional.of(conceptThree));
        given(relationshipDao.getRelationships(conceptTwo.getId()))
                .willReturn(Sets.newHashSet(schemeHasTopConceptTwo, threeIsRelatedToTwo));
    }

    private void givenEntitiesRelatedToConceptSchemeExist() throws SkosPersistenceException {
        given(conceptSchemeDao.read(conceptScheme.getId())).willReturn(Optional.of(conceptScheme));
        given(conceptDao.read(conceptOne.getId())).willReturn(Optional.of(conceptOne));
        given(conceptDao.read(conceptTwo.getId())).willReturn(Optional.of(conceptTwo));
        given(relationshipDao.getRelationships(conceptScheme.getId()))
                .willReturn(Sets.newHashSet(schemeHasTopConceptOne, schemeHasTopConceptTwo));
    }

    private Model givenSkosOutput(RdfModel input) {
        Model model = mock(Model.class);
        given(skosTranslator.translate(argThat(new EqualsRdfModel(input)))).willReturn(model);
        return model;
    }

    private class EqualsRdfModel implements ArgumentMatcher<RdfModel> {

        private RdfModel expected;

        private EqualsRdfModel(RdfModel expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(RdfModel argument) {
            return expected.getConcepts().equals(argument.getConcepts())
                    && expected.getConceptSchemes().equals(argument.getConceptSchemes())
                    && expected.getRelationships().equals(argument.getRelationships());
        }
    }
}
