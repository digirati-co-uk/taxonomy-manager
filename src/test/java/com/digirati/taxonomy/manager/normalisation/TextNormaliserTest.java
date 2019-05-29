package com.digirati.taxonomy.manager.normalisation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TextNormaliserTest {

    @Test
    void normaliseShouldLemmatizeText() {
        // Given
        TextNormaliser textNormaliser = new TextNormaliser(new ArrayList<>());

        // When
        String actual = textNormaliser.normalise("I was there yesterday");

        // Then
        String expected = "I be there yesterday";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldExpandContractedEnglishWords() {
        // Given
        TextNormaliser textNormaliser = new TextNormaliser(new ArrayList<>());

        // When
        String actual = textNormaliser.normalise("don't won't shouldn't can't I'm");

        // Then
        String expected = "do not will not should not can not I be";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldLemmatizeBritishAndAmericanSpellings() {
        // Given
        TextNormaliser textNormaliser = new TextNormaliser(new ArrayList<>());

        // When
        String actual = textNormaliser.normalise("coloring colouring");

        // Then
        String expected = "color colour";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldTokeniseWordsInNonEnglishLanguage() {
        // Given
        TextNormaliser textNormaliser = new TextNormaliser(new ArrayList<>(), "fr", "French");

        // When
        String actual = textNormaliser.normalise("qu'est que c'est");

        // Then
        String expected = "qu' est que c' est";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldRemoveStopwords() {
        // Given
        TextNormaliser textNormaliser =
                new TextNormaliser(
                        Arrays.asList(
                                "in", // Generic stopword
                                "the", // Generic stopword
                                "be", // lemmatized version of "was"
                                "thought" // non-lemmatized version of "think"
                                ));

        // When
        String actual =
                textNormaliser.normalise(
                        "I was there yesterday in the afternoon and it was better than I thought");

        // Then
        String expected = "I there yesterday afternoon and it better than I";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldNotForceRemovalOfNonLatinCharacters() {
        // Given
        TextNormaliser textNormaliser = new TextNormaliser(new ArrayList<>());

        // When
        String actual = textNormaliser.normalise("הייתי שם אתמול אחר הצהריים");

        // Then
        String expected = "הייתי שם אתמול אחר הצהריים";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldTrimWhitespace() {
        // Given
        TextNormaliser textNormaliser = new TextNormaliser(new ArrayList<>());

        // When
        String actual = textNormaliser.normalise("       what		\n");

        // Then
        String expected = "what";
        assertEquals(expected, actual);
    }
}
