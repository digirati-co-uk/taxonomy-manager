package com.digirati.taxonomy.manager.normalisation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TextNormaliserTest {

    @Mock private StanfordCoreNLP mockPipeline;

    @Mock private CoreDocument mockDocument;

    @Test
    void normaliseShouldLemmatizeText() throws ExecutionException, InterruptedException {
        // Given
        TextNormaliser textNormaliser =
                TextNormaliser.initialiseEnglishNormaliser(new HashSet<>()).get();

        // When
        String actual = textNormaliser.normalise("I was there yesterday");

        // Then
        String expected = "I be there yesterday";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldTokeniseWordsInNonEnglishLanguage()
            throws ExecutionException, InterruptedException {
        // Given
        TextNormaliser textNormaliser =
                TextNormaliser.initialiseNormaliser(new HashSet<>(), "fr", "French").get();

        // When
        String actual = textNormaliser.normalise("qu'est que c'est");

        // Then
        String expected = "qu' est que c' est";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldRemoveStopwords() {
        // Given
        givenMockedDocumentHasLemmas(
                mockedWord("I"),
                mockedWord("was", "be"),
                mockedWord("there"),
                mockedWord("yesterday"),
                mockedWord("in"),
                mockedWord("the"),
                mockedWord("afternoon"),
                mockedWord("and"),
                mockedWord("it"),
                mockedWord("was", "be"),
                mockedWord("better"),
                mockedWord("than"),
                mockedWord("I"),
                mockedWord("thought", "think"));
        Set<String> stopwords =
                Sets.newHashSet(
                        "in", // Generic stopword
                        "the", // Generic stopword
                        "be", // lemmatized version of "was"
                        "thought" // non-lemmatized version of "think"
                        );
        TextNormaliser textNormaliser =
                new TextNormaliser(stopwords, mockPipeline, text -> mockDocument);

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
        givenMockedDocumentHasLemmas(
                mockedWord("הייתי"),
                mockedWord("שם"),
                mockedWord("אתמול"),
                mockedWord("אחר"),
                mockedWord("הצהריים"));
        TextNormaliser textNormaliser =
                new TextNormaliser(new HashSet<>(), mockPipeline, text -> mockDocument);

        // When
        String actual = textNormaliser.normalise("הייתי שם אתמול אחר הצהריים");

        // Then
        String expected = "הייתי שם אתמול אחר הצהריים";
        assertEquals(expected, actual);
    }

    @Test
    void normaliseShouldTrimWhitespace() {
        // Given
        givenMockedDocumentHasLemmas(mockedWord("      "), mockedWord("what"), mockedWord("		\n"));
        TextNormaliser textNormaliser =
                new TextNormaliser(new HashSet<>(), mockPipeline, text -> mockDocument);

        // When
        String actual = textNormaliser.normalise("       what		\n");

        // Then
        String expected = "what";
        assertEquals(expected, actual);
    }

    private void givenMockedDocumentHasLemmas(CoreLabel... words) {
        given(mockDocument.tokens()).willReturn(Lists.newArrayList(words));
    }

    private CoreLabel mockedWord(String originalForm, String lemmaForm) {
        CoreLabel mockedWord = mock(CoreLabel.class);
        lenient().when(mockedWord.originalText()).thenReturn(originalForm);
        lenient().when(mockedWord.lemma()).thenReturn(lemmaForm);
        return mockedWord;
    }

    private CoreLabel mockedWord(String word) {
        return mockedWord(word, word);
    }
}
