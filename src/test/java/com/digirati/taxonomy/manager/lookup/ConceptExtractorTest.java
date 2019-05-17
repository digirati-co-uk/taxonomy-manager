package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Concept;
import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConceptExtractorTest {

    @Test
    void extractShouldReturnCollectionWithAllMatchingConcepts() {
        // Given
        Concept fight = new Concept("fight", Lists.newArrayList("row"));
        Concept line = new Concept("line", Lists.newArrayList("row"));
        Concept purpose = new Concept("purpose", Lists.newArrayList("sake"));
        Concept riceWine = new Concept("rice wine", Lists.newArrayList("sake"));

        ConceptExtractor underTest =
                new ConceptExtractor(
                        new AhoCorasickTextSearcher(Lists.newArrayList("row", "sake")));
        Multimap<String, Concept> conceptLookupTable = underTest.getConceptLookupTable();
        conceptLookupTable.putAll("row", Arrays.asList(fight, line));
        conceptLookupTable.putAll("sake", Arrays.asList(purpose, riceWine));

        // When
        Collection<ConceptMatch> actual =
                underTest.extract(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        Collection<ConceptMatch> expected =
                Lists.newArrayList(
                        match("row", 2, 4, fight, line),
                        match("sake", 24, 27, purpose, riceWine),
                        match("row", 62, 64, fight, line),
                        match("sake", 69, 72, purpose, riceWine));
        assertEquals(expected, actual);
    }

    @Test
    void extractShouldBeAbleToHandleMatchedTermsWithNoAttachedConcepts() {
        // Given
        ConceptExtractor underTest =
                new ConceptExtractor(
                        new AhoCorasickTextSearcher(Lists.newArrayList("row", "sake")));

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

    private ConceptMatch match(String term, int startIndex, int endIndex, Concept... concepts) {
        return new ConceptMatch(
                new TermMatch(term, startIndex, endIndex), Lists.newArrayList(concepts));
    }
}
