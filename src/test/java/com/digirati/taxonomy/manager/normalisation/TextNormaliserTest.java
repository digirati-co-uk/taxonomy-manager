package com.digirati.taxonomy.manager.normalisation;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TextNormaliserTest {

    @Test
    void normaliseShouldLemmatizeText() {
        // Given
        TextNormaliser textNormaliser = TextNormaliser.initialiseEnglishNormaliser(new HashSet<>());

        // When
        String actual = textNormaliser.normalise("I was there yesterday");

        // Then
        String expected = "I be there yesterday";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldTokeniseWordsInNonEnglishLanguage() {
        // Given
        TextNormaliser textNormaliser =
                TextNormaliser.initialiseNormaliser(new HashSet<>(), "fr", "French");

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
                TextNormaliser.initialiseEnglishNormaliser(
                        Sets.newHashSet(
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
        TextNormaliser textNormaliser = TextNormaliser.initialiseEnglishNormaliser(new HashSet<>());

        // When
        String actual = textNormaliser.normalise("הייתי שם אתמול אחר הצהריים");

        // Then
        String expected = "הייתי שם אתמול אחר הצהריים";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldTrimWhitespace() {
        // Given
        TextNormaliser textNormaliser = TextNormaliser.initialiseEnglishNormaliser(new HashSet<>());

        // When
        String actual = textNormaliser.normalise("       what		\n");

        // Then
        String expected = "what";
        assertEquals(expected, actual);
    }
}
