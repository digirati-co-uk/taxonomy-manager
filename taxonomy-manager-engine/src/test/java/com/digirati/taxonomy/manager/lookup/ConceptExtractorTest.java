package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConceptExtractorTest {

	@Mock
	private TextSearcher mockTextSearcher;

	private Multimap<String, UUID> termToId;

	private ConceptExtractor underTest;

	@BeforeEach
	void setup() {
		lenient().when(mockTextSearcher.rebuild(anySet())).thenReturn(mockTextSearcher);
		termToId = ArrayListMultimap.create();
		underTest = new ConceptExtractor(mockTextSearcher, termToId);
	}

    @Test
    void extractShouldReturnCollectionWithAllMatchingConcepts() {
        // Given
        UUID fightId = UUID.randomUUID();
        UUID lineId = UUID.randomUUID();
        UUID purposeId = UUID.randomUUID();
        UUID riceWineId = UUID.randomUUID();

        ConceptExtractor underTest =
                new ConceptExtractor(
                        new AhoCorasickTextSearcher(Lists.newArrayList("row", "sake")),
                        ArrayListMultimap.create());
        Multimap<String, UUID> conceptLookupTable = underTest.getConceptLookupTable();
        conceptLookupTable.putAll("row", Arrays.asList(fightId, lineId));
        conceptLookupTable.putAll("sake", Arrays.asList(purposeId, riceWineId));

        // When
        Collection<ConceptMatch> actual =
                underTest.extract(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        Collection<ConceptMatch> expected =
                Lists.newArrayList(
                        match("row", 2, 4, fightId, lineId),
                        match("sake", 24, 27, purposeId, riceWineId),
                        match("row", 62, 64, fightId, lineId),
                        match("sake", 69, 72, purposeId, riceWineId));
        assertEquals(expected, actual);
    }

    @Test
    void extractShouldBeAbleToHandleMatchedTermsWithNoAttachedConcepts() {
        // Given
        ConceptExtractor underTest =
                new ConceptExtractor(
                        new AhoCorasickTextSearcher(Lists.newArrayList("row", "sake")),
                        ArrayListMultimap.create());

        // When
        Collection<ConceptMatch> actual =
                underTest.extract(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        Collection<ConceptMatch> expected =
                Lists.newArrayList(
                        match("row", 2, 4),
                        match("sake", 24, 27),
                        match("row", 62, 64),
                        match("sake", 69, 72));
        assertEquals(expected, actual);
    }

    @Test
    void addConceptShouldRebuildLookupTableAndSearcherWhenLabelsAreNew() {
        // Given
		UUID conceptUuid = UUID.randomUUID();

        // When
        underTest.addConcept(conceptUuid, Sets.newHashSet("a"));

        // Then
		assertEquals(Lists.newArrayList(conceptUuid), termToId.get("a"));
		verify(mockTextSearcher).rebuild(Sets.newHashSet("a"));
    }

    @Test
    void addConceptShouldNotRebuildSearcherWhenLabelsAreAlreadyLoaded() {
		// Given
		UUID originalConceptUuid = UUID.randomUUID();
		termToId.put("a", originalConceptUuid);

		UUID newConceptUuid = UUID.randomUUID();

		// When
		underTest.addConcept(newConceptUuid, Sets.newHashSet("a"));

		// Then
		assertEquals(Lists.newArrayList(originalConceptUuid, newConceptUuid), termToId.get("a"));
		verify(mockTextSearcher, never()).rebuild(Sets.newHashSet("a"));
    }

    @Test
    void updateConceptShouldRebuildLookupTableAndSearcherWhenLabelsHaveChanged() {
        // Given
		UUID conceptUuid = UUID.randomUUID();
		termToId.put("a", conceptUuid);
		termToId.put("b", conceptUuid);

		// When
		underTest.updateConcept(conceptUuid, Sets.newHashSet("c", "d"));

		// Then
		assertEquals(Lists.newArrayList(), termToId.get("a"));
		assertEquals(Lists.newArrayList(), termToId.get("b"));
		assertEquals(Lists.newArrayList(conceptUuid), termToId.get("c"));
		assertEquals(Lists.newArrayList(conceptUuid), termToId.get("d"));
		verify(mockTextSearcher).rebuild(Sets.newHashSet("c", "d"));
    }

    @Test
    void updateConceptShouldNotRebuildWhenLabelsHaveNotChanged() {
		// Given
		UUID conceptUuid = UUID.randomUUID();
		termToId.put("a", conceptUuid);
		termToId.put("b", conceptUuid);

		// When
		underTest.updateConcept(conceptUuid, Sets.newHashSet("a", "b"));

		// Then
		assertEquals(Lists.newArrayList(conceptUuid), termToId.get("a"));
		assertEquals(Lists.newArrayList(conceptUuid), termToId.get("b"));
		verify(mockTextSearcher, never()).rebuild(anySet());
    }

    @Test
    void removeConceptShouldRebuildSearcherWhenLabelsNoLongerExist() {
		// Given
		UUID deletedConceptUuid = UUID.randomUUID();
		UUID otherConceptUuid = UUID.randomUUID();
		termToId.put("a", deletedConceptUuid);
		termToId.put("a", otherConceptUuid);
		termToId.put("b", deletedConceptUuid);

		// When
		underTest.removeConcept(deletedConceptUuid, Sets.newHashSet("a", "b"));

		// Then
		assertEquals(Lists.newArrayList(otherConceptUuid), termToId.get("a"));
		assertEquals(Lists.newArrayList(), termToId.get("b"));
		verify(mockTextSearcher).rebuild(Sets.newHashSet("a"));
    }

    @Test
    void removeConceptShouldNotRebuildSearcherWhenLabelsExistForOtherConcepts() {
		// Given
		UUID deletedConceptUuid = UUID.randomUUID();
		UUID otherConceptUuid = UUID.randomUUID();
		termToId.put("a", deletedConceptUuid);
		termToId.put("a", otherConceptUuid);
		termToId.put("b", deletedConceptUuid);
		termToId.put("b", otherConceptUuid);

		// When
		underTest.removeConcept(deletedConceptUuid, Sets.newHashSet("a", "b"));

		// Then
		assertEquals(Lists.newArrayList(otherConceptUuid), termToId.get("a"));
		assertEquals(Lists.newArrayList(otherConceptUuid), termToId.get("b"));
		verify(mockTextSearcher, never()).rebuild(anySet());
    }

    private ConceptMatch match(String term, int startIndex, int endIndex, UUID... concepts) {
        return new ConceptMatch(
                new TermMatch(term, startIndex, endIndex), Lists.newArrayList(concepts));
    }
}
