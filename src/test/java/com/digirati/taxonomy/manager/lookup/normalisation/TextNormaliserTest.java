package com.digirati.taxonomy.manager.lookup.normalisation;

import com.digirati.taxonomy.manager.lookup.model.Word;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TextNormaliserTest {

    @Test
    void extractContentWordsShouldExtractWordsLocationsAndLemmas() {
        // Given
        TextNormaliser textNormaliser = TextNormaliser.initialiseEnglishNormaliser(new HashSet<>());

        // When
        List<Word> actual = textNormaliser.extractContentWords("I was there yesterday");

        // Then
        List<Word> expected =
                Arrays.asList(
                        new Word("I", 0, 1, "I"),
                        new Word("was", 2, 5, "be"),
                        new Word("there", 6, 11, "there"),
                        new Word("yesterday", 12, 21, "yesterday"));
        assertEquals(expected, actual);
    }

    @Test
    void extractContentWordsShouldExcludeStopwords() {
        // Given
        TextNormaliser textNormaliser =
                TextNormaliser.initialiseEnglishNormaliser(Sets.newHashSet("I", "be"));

        // When
        List<Word> actual = textNormaliser.extractContentWords("I was there yesterday");

        // Then
        List<Word> expected =
                Arrays.asList(
                        new Word("there", 6, 11, "there"),
                        new Word("yesterday", 12, 21, "yesterday"));
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldCollapseContentWordsIntoLemmatisedText() {
        // Given
        TextNormaliser textNormaliser = TextNormaliser.initialiseEnglishNormaliser(new HashSet<>());
        List<Word> contentWords =
                Arrays.asList(
                        new Word("I", 0, 1, "I"),
                        new Word("was", 2, 5, "be"),
                        new Word("there", 6, 11, "there"),
                        new Word("yesterday", 12, 21, "yesterday"));

        // When
        String actual = textNormaliser.normalise(contentWords);

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
