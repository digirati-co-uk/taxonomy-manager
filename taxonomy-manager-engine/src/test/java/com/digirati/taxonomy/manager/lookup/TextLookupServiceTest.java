package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.Word;
import com.digirati.taxonomy.manager.lookup.model.LookupResultContext;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.digirati.taxonomy.manager.lookup.normalisation.TextNormaliser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TextLookupServiceTest {

    @Test
    void searchShouldProvideFullyPopulatedLookupResultContext() throws ExecutionException, InterruptedException {
        // Given
        String inputText = "\n	  	I was there yesterday    	";

        Collection<String> terms = Arrays.asList("there", "yesterday");
        Set<String> stopwords = Sets.newHashSet("I");

        TextSearcher textSearcher = new AhoCorasickTextSearcher(terms);
        ConceptExtractor conceptExtractor = new ConceptExtractor(textSearcher, ArrayListMultimap.create());
        TextNormaliser textNormaliser = TextNormaliser.initialiseEnglishNormaliser(stopwords).get();
        TextLookupService underTest = new TextLookupService(textNormaliser, conceptExtractor, "en");

        // When
        LookupResultContext actual = underTest.search(inputText);

        // Then
        String normalisedText = "be there yesterday";
        List<Word> contentWords =
                Arrays.asList(
                        new Word("was", 7, 10, "be"),
                        new Word("there", 11, 16, "there"),
                        new Word("yesterday", 17, 26, "yesterday"));
        Collection<ConceptMatch> conceptMatches =
                Arrays.asList(conceptMatch("there", 11, 16), conceptMatch("yesterday", 17, 26));
        LookupResultContext expected =
                new LookupResultContext(inputText, normalisedText, contentWords, conceptMatches);

        assertEquals(expected, actual);
    }

    @Test
    void addConceptShouldAddNormalisedConceptLabelsToExtractor() {
        fail();
    }

    @Test
    void updateConceptShouldApplyNormalisedConceptLabelsToExtractor() {
        fail();
    }

    @Test
    void removeConceptShouldRemoveNormalisedLabelsFromExtractor() {
        fail();
    }

    private ConceptMatch conceptMatch(String originalTerm, int startIndex, int endIndex) {
        TermMatch termMatch = new TermMatch(originalTerm, startIndex, endIndex);
        return new ConceptMatch(termMatch, new ArrayList<>());
    }
}
