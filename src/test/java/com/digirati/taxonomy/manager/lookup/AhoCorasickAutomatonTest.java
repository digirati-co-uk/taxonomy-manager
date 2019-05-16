package com.digirati.taxonomy.manager.lookup;

import org.ahocorasick.trie.Emit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class AhoCorasickAutomatonTest {

    @Test
    void searchShouldReturnAllMatchingTerms() {
        // Given
        AhoCorasickAutomaton underTest =
                new AhoCorasickAutomaton("really", "disappointing", "game of thrones");

        // When
        Collection<Emit> matches =
                underTest.search(
                        "last night's episode of game of thrones was really disappointing");

        // Then
        assertMatches(
                matches,
                match("game of thrones", 24, 38),
                match("really", 44, 49),
                match("disappointing", 51, 63));
    }

    @Test
    void searchShouldMatchTermsWithDifferentCase() {
        // Given
        AhoCorasickAutomaton underTest =
                new AhoCorasickAutomaton("really", "DISAPPOINTING", "game of thrones");

        // When
        Collection<Emit> matches =
                underTest.search(
                        "last night's episode of Game of Thrones was REALLY disappointing");

        // Then
        assertMatches(
                matches,
                match("game of thrones", 24, 38),
                match("really", 44, 49),
                match("DISAPPOINTING", 51, 63));
    }

    @Test
    void searchShouldNotMatchTermsWithinWords() {
        // Given
        AhoCorasickAutomaton underTest =
                new AhoCorasickAutomaton("game of thrones", "game", "am", "a");

        // When
        Collection<Emit> matches = underTest.search("game of thrones");

        // Then
        assertMatches(matches, match("game", 0, 3), match("game of thrones", 0, 14));
    }

    @Test
    void searchShouldNotMatchTermsSeparatedByPunctuation() {
        // Given
        AhoCorasickAutomaton underTest = new AhoCorasickAutomaton("aho", "corasick");

        // When
        Collection<Emit> matches = underTest.search("aho-corasick");

        // Then
        MatcherAssert.assertThat(matches, Matchers.iterableWithSize(0));
    }

    private void assertMatches(Collection<Emit> actual, Emit... expected) {
        MatcherAssert.assertThat(actual, Matchers.contains(expected));
    }

    private Emit match(String term, int startIndex, int endIndex) {
        return new Emit(startIndex, endIndex, term);
    }
}
