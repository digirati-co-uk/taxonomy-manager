package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Concept;
import com.digirati.taxonomy.manager.lookup.model.Match;
import com.google.common.collect.Lists;
import org.ahocorasick.trie.Emit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class LookupTest {

    @Test
    void searchShouldReturnCollectionWithAllMatchingConcepts() {
        // Given
        Concept fight = new Concept("fight", Lists.newArrayList("row"));
        Concept line = new Concept("line", Lists.newArrayList("row"));
        Concept purpose = new Concept("purpose", Lists.newArrayList("sake"));
        Concept riceWine = new Concept("rice wine", Lists.newArrayList("sake"));

        Lookup underTest = new Lookup("row", "sake");
        ConceptLookupTable conceptLookupTable = underTest.getConceptLookupTable();
        conceptLookupTable.put("row", fight, line);
        conceptLookupTable.put("sake", purpose, riceWine);

        // When
        Collection<Match> matches =
                underTest.search(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        assertMatches(
                matches,
                match("row", 2, 4, fight, line),
                match("sake", 24, 27, purpose, riceWine),
                match("row", 62, 64, fight, line),
                match("sake", 69, 72, purpose, riceWine));
    }

    @Test
    void searchShouldBeAbleToHandleMatchedTermsWithNoAttachedConcepts() {
        // Given
        Lookup underTest = new Lookup("row", "sake");

        // When
        Collection<Match> matches =
                underTest.search(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        assertMatches(
                matches,
                match("row", 2, 4),
                match("sake", 24, 27),
                match("row", 62, 64),
                match("sake", 69, 72));
    }

    @Test
    void rebuildAutomatonShouldReplaceOriginalTermSet() {
        // Given
        Lookup underTest = new Lookup("row", "sake");
        underTest.rebuildAutomaton("drink", "fastest");

        // When
        Collection<Match> matches =
                underTest.search(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        assertMatches(matches, match("drink", 54, 58), match("fastest", 78, 84));
    }

    @Test
    void clearConceptLookupTableShouldRemoveAnyExistingConceptMappings() {
        // Given
        Lookup underTest = new Lookup("row", "sake");
        ConceptLookupTable conceptLookupTable = underTest.getConceptLookupTable();
        conceptLookupTable.put(
                "row",
                new Concept("fight", Lists.newArrayList("row")),
                new Concept("line", Lists.newArrayList("row")));
        conceptLookupTable.put(
                "sake",
                new Concept("purpose", Lists.newArrayList("sake")),
                new Concept("rice wine", Lists.newArrayList("sake")));

        underTest.clearConceptLookupTable();

        // When
        Collection<Match> matches =
                underTest.search(
                        "a row broke out for the sake of working out who could drink a row of sake the fastest");

        // Then
        assertMatches(
                matches,
                match("row", 2, 4),
                match("sake", 24, 27),
                match("row", 62, 64),
                match("sake", 69, 72));
    }

    private void assertMatches(Collection<Match> actual, Match... expected) {
        MatcherAssert.assertThat(actual, Matchers.contains(expected));
    }

    private Match match(String term, int startIndex, int endIndex) {
        return new Match(new Emit(startIndex, endIndex, term), null);
    }

    private Match match(String term, int startIndex, int endIndex, Concept... concepts) {
        return new Match(new Emit(startIndex, endIndex, term), Lists.newArrayList(concepts));
    }
}
