package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.Term;
import com.digirati.taxonomy.manager.lookup.model.Word;
import com.digirati.taxonomy.manager.lookup.model.LookupResultContext;
import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.digirati.taxonomy.manager.lookup.normalisation.TextNormaliser;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Service to orchestrate all the steps in looking up concepts from a piece of input text. */
public class TextLookupService {

    private static final Set<String> stopWords = Sets.newHashSet(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with"
    );

    private final TextNormaliser textNormaliser;

    private final ConceptExtractor conceptExtractor;

    private final String languageKey;

    public static Future<TextLookupService> initialiseLookupService(
            Stream<Term> concepts,
            String languageKey,
            String languageName) {

        return Executors.newSingleThreadExecutor().submit(() -> init(concepts, languageKey, languageName));
    }

    private static TextLookupService init(
            Stream<Term> concepts,
            String languageKey,
            String languageName)
            throws ExecutionException, InterruptedException {

        TextNormaliser textNormaliser =
                TextNormaliser.initialiseNormaliser(stopWords, languageKey, languageName).get();

        ArrayListMultimap<String, UUID> termToUuid = ArrayListMultimap.create();
        concepts.forEach(concept -> {
            UUID conceptUuid = concept.getUuid();
            concept.getLabels()
                    .forEach(label ->  termToUuid.put(textNormaliser.normalise(label), conceptUuid));
        });

        Set<String> terms = termToUuid.keySet();

        ConceptExtractor conceptExtractor =
                new ConceptExtractor(new AhoCorasickTextSearcher(terms), termToUuid);
        return new TextLookupService(textNormaliser, conceptExtractor, languageKey);
    }

    @VisibleForTesting
    TextLookupService(
            TextNormaliser textNormaliser, ConceptExtractor conceptExtractor, String languageKey) {
        this.textNormaliser = textNormaliser;
        this.conceptExtractor = conceptExtractor;
        this.languageKey = languageKey;
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
        int contentWordIndex = getContentWordIndex(previousWords);
        Word contentWord = contentWords.get(contentWordIndex);
        TermMatch originalTerm =
                new TermMatch(
                        contentWord.getOriginalText(),
                        contentWord.getOriginalStartPosition(),
                        contentWord.getOriginalEndPosition());
        return new ConceptMatch(originalTerm, normalisedMatch.getConceptIds());
    }

    private int getContentWordIndex(String[] previousWords) {
        if (previousWords.length == 1 && "".equals(previousWords[0])) {
            return 0;
        }
        return previousWords.length;
    }

    public void addConcept(ConceptModel concept) {
        UUID conceptUuid = concept.getUuid();
        Set<String> labels = concept.getLabels(languageKey)
                .map(textNormaliser::normalise)
                .collect(Collectors.toSet());

        conceptExtractor.addConcept(conceptUuid, labels);
    }

    public void updateConcept(ConceptModel concept) {
        UUID conceptUuid = concept.getUuid();
        Set<String> labels = concept.getLabels(languageKey)
                .map(textNormaliser::normalise)
                .collect(Collectors.toSet());

        conceptExtractor.updateConcept(conceptUuid, labels);
    }

    public void removeConcept(ConceptModel concept) {
        UUID conceptUuid = concept.getUuid();
        Set<String> labels = concept.getLabels(languageKey)
                .map(textNormaliser::normalise)
                .collect(Collectors.toSet());

        conceptExtractor.removeConcept(conceptUuid, labels);
    }
}
