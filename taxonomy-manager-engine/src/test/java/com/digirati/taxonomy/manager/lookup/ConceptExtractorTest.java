package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ConceptExtractorTest {

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
        fail();
    }

    @Test
    void addConceptShouldNotRebuildSearcherWhenLabelsAreAlreadyLoaded() {
        fail();
    }

    @Test
    void updateConceptShouldRebuildLookupTableAndSearcherWhenLabelsHaveChanged() {
        fail();
    }

    @Test
    void updateConceptShouldNotRebuildWhenLabelsHaveNotChanged() {
        fail();
    }

    @Test
    void removeConceptShouldRebuildSearcherWhenLabelsNoLongerExist() {
        fail();
    }

    @Test
    void removeConceptShouldNotRebuildSearcherWhenLabelsExistForOtherConcepts() {
        fail();
    }

    private ConceptMatch match(String term, int startIndex, int endIndex, UUID... concepts) {
        return new ConceptMatch(
                new TermMatch(term, startIndex, endIndex), Lists.newArrayList(concepts));
    }
}
