package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AhoCorasickTextSearcherTest {

    @Test
    void searchShouldReturnAllMatchingTerms() {
        // Given
        AhoCorasickTextSearcher underTest =
                new AhoCorasickTextSearcher(
                        Arrays.asList("really", "disappointing", "game of thrones"));

        // When
        Collection<TermMatch> actual =
                underTest
                        .search("last night's episode of game of thrones was really disappointing")
                        .collect(Collectors.toList());

        // Then
        Collection<TermMatch> expected =
                Lists.newArrayList(
                        new TermMatch("game of thrones", 24, 38),
                        new TermMatch("really", 44, 49),
                        new TermMatch("disappointing", 51, 63));
        assertEquals(expected, actual);
    }

    @Test
    void searchShouldMatchTermsWithDifferentCase() {
        // Given
        AhoCorasickTextSearcher underTest =
                new AhoCorasickTextSearcher(
                        Arrays.asList("really", "DISAPPOINTING", "gAmE oF tHrOnEs"));

        // When
        Collection<TermMatch> actual =
                underTest
                        .search("last night's episode of Game of Thrones was REALLY disappointing")
                        .collect(Collectors.toList());

        // Then
        Collection<TermMatch> expected =
                Lists.newArrayList(
                        new TermMatch("game of thrones", 24, 38),
                        new TermMatch("really", 44, 49),
                        new TermMatch("disappointing", 51, 63));
        assertEquals(expected, actual);
    }

    @Test
    void searchShouldNotMatchTermsWithinWords() {
        // Given
        AhoCorasickTextSearcher underTest =
                new AhoCorasickTextSearcher(Arrays.asList("game of thrones", "game", "am", "a"));

        // When
        Collection<TermMatch> actual =
                underTest.search("game of thrones").collect(Collectors.toList());

        // Then
        Collection<TermMatch> expected =
                Lists.newArrayList(
                        new TermMatch("game", 0, 3), new TermMatch("game of thrones", 0, 14));
        assertEquals(expected, actual);
    }

    @Test
    void searchShouldNotMatchTermsSeparatedByPunctuation() {
        // Given
        AhoCorasickTextSearcher underTest =
                new AhoCorasickTextSearcher(Arrays.asList("aho", "corasick"));

        // When
        Collection<TermMatch> actual =
                underTest.search("aho-corasick").collect(Collectors.toList());

        // Then
        assertTrue(actual.isEmpty());
    }
}
