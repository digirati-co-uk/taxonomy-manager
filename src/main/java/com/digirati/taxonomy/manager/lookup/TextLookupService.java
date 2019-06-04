package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Word;
import com.digirati.taxonomy.manager.lookup.model.LookupResultContext;
import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.digirati.taxonomy.manager.lookup.normalisation.TextNormaliser;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Service to orchestrate all the steps in looking up concepts from a piece of input text. */
public class TextLookupService {

    private final TextNormaliser textNormaliser;

    private final ConceptExtractor conceptExtractor;

    public TextLookupService(TextNormaliser textNormaliser, ConceptExtractor conceptExtractor) {
        this.textNormaliser = textNormaliser;
        this.conceptExtractor = conceptExtractor;
    }

    /**
     * Searches a given input string for all occurrences of any concepts provided to the concept
     * extractor.
     *
     * @param inputText the text in which to search
     * @return a {@link LookupResultContext} containing details of what was searched and any
     *     concepts that were found
     */
    public LookupResultContext search(String inputText) {
        List<Word> contentWords = textNormaliser.extractContentWords(inputText);
        String normalisedText = textNormaliser.normalise(contentWords);
        Collection<ConceptMatch> normalisedConceptMatches =
                conceptExtractor.extract(normalisedText);
        Collection<ConceptMatch> denormalisedConceptMatches =
                normalisedConceptMatches.stream()
                        .map(
                                normalisedMatch ->
                                        createDenormalisedMatch(
                                                normalisedMatch, normalisedText, contentWords))
                        .collect(Collectors.toList());
        return new LookupResultContext(
                inputText, normalisedText, contentWords, denormalisedConceptMatches);
    }

    /**
     * Converts a {@link ConceptMatch} found in the normalised form of a piece of text into one
     * corresponding to the concept in the original, non-normalised form of that piece of text.
     *
     * @param normalisedMatch the details of the concept as it appeared in the normalised text
     * @param normalisedText the normalised form of the text which was searched
     * @param contentWords a list of the {@link Word}s which map the terms in the normalised text
     *     back to the original form
     * @return a {@link ConceptMatch} detailing where the concept was found in the original text
     */
    private ConceptMatch createDenormalisedMatch(
            ConceptMatch normalisedMatch, String normalisedText, List<Word> contentWords) {
        int normalisedTermStartIndex = normalisedMatch.getTermMatch().getStartIndex();
        String[] previousWords = normalisedText.substring(0, normalisedTermStartIndex).split(" ");
        int contentWordIndex = previousWords.length;
        Word contentWord = contentWords.get(contentWordIndex);
        TermMatch originalTerm =
                new TermMatch(
                        contentWord.getOriginalText(),
                        contentWord.getOriginalStartPosition(),
                        contentWord.getOriginalEndPosition());
        return new ConceptMatch(originalTerm, normalisedMatch.getConcepts());
    }
}
