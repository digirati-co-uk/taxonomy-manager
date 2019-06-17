package com.digirati.taxonomy.manager.lookup.normalisation;

import com.digirati.taxonomy.manager.lookup.model.Word;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TextNormaliserTest {

    @Mock private StanfordCoreNLP mockPipeline;

    @Mock private CoreDocument mockDocument;

    @Test
    void extractContentWordsShouldExtractWordsLocationsAndLemmas()
            throws ExecutionException, InterruptedException {
        // Given
        TextNormaliser textNormaliser =
                TextNormaliser.initialiseEnglishNormaliser(new HashSet<>()).get();

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
    void extractContentWordsShouldExcludeNormalisedStopwords() {
        // Given
        Map<String, CoreDocument> inputToNormalisedDocument = new HashMap<>();
        inputToNormalisedDocument.put("I", mockedDocumentWithWords(mockedWord("I")));
        inputToNormalisedDocument.put("is", mockedDocumentWithWords(mockedWord("is", "be", 0, 2)));
        inputToNormalisedDocument.put(
                "I was there yesterday",
                mockedDocumentWithWords(
                        mockedWord("I"),
                        mockedWord("was", "be", 2, 5),
                        mockedWord("there", "there", 6, 11),
                        mockedWord("yesterday", "yesterday", 12, 21)));

        TextNormaliser textNormaliser =
                new TextNormaliser(
                        Sets.newHashSet("I", "is"), mockPipeline, inputToNormalisedDocument::get);

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
        TextNormaliser textNormaliser =
                new TextNormaliser(new HashSet<>(), mockPipeline, text -> mockDocument);
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
    void normaliseShouldTokeniseWordsInNonEnglishLanguage()
            throws ExecutionException, InterruptedException {
        // Given
        TextNormaliser textNormaliser =
                TextNormaliser.initialiseNormaliser(new HashSet<>(), "fr", "French").get();

        // When
        String actual =
                textNormaliser.normalise(textNormaliser.extractContentWords("qu'est que c'est"));

        // Then
        String expected = "qu' est que c' est";
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
        String actual =
                textNormaliser.normalise(
                        textNormaliser.extractContentWords("הייתי שם אתמול אחר הצהריים"));

        // Then
        String expected = "הייתי שם אתמול אחר הצהריים";
        assertEquals(expected, actual);
    }

    private void givenMockedDocumentHasLemmas(CoreLabel... words) {
        given(mockDocument.tokens()).willReturn(Lists.newArrayList(words));
    }

    private CoreDocument mockedDocumentWithWords(CoreLabel... words) {
        CoreDocument coreDocument = mock(CoreDocument.class);
        given(coreDocument.tokens()).willReturn(Lists.newArrayList(words));
        return coreDocument;
    }

    private CoreLabel mockedWord(
            String originalForm, String lemmaForm, int startIndex, int endIndex) {
        CoreLabel mockedWord = mock(CoreLabel.class);
        lenient().when(mockedWord.originalText()).thenReturn(originalForm);
        lenient().when(mockedWord.lemma()).thenReturn(lemmaForm);
        lenient().when(mockedWord.beginPosition()).thenReturn(startIndex);
        lenient().when(mockedWord.endPosition()).thenReturn(endIndex);
        return mockedWord;
    }

    private CoreLabel mockedWord(String word) {
        return mockedWord(word, word, 0, 0);
    }
}
