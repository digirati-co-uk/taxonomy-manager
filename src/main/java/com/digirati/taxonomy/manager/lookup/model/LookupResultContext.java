package com.digirati.taxonomy.manager.lookup.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Models the context around the results of a lookup, including the original and normalised forms of
 * the searched text, the mapping between original words and normalised words, and details of any
 * matched concepts
 */
public class LookupResultContext {

    private final String originalText;

    private final String normalisedText;

    private final List<Word> contentWords;

    private final Collection<ConceptMatch> matchedConcepts;

    public LookupResultContext(
            String originalText,
            String normalisedText,
            List<Word> contentWords,
            Collection<ConceptMatch> matchedConcepts) {
        this.originalText = originalText;
        this.normalisedText = normalisedText;
        this.contentWords = contentWords;
        this.matchedConcepts = matchedConcepts;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getNormalisedText() {
        return normalisedText;
    }

    public List<Word> getContentWords() {
        return contentWords;
    }

    public Collection<ConceptMatch> getMatchedConcepts() {
        return matchedConcepts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LookupResultContext that = (LookupResultContext) o;
        return Objects.equals(originalText, that.originalText)
                && Objects.equals(normalisedText, that.normalisedText)
                && Objects.equals(contentWords, that.contentWords)
                && Objects.equals(matchedConcepts, that.matchedConcepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalText, normalisedText, contentWords, matchedConcepts);
    }
}
