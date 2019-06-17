package com.digirati.taxonomy.manager.lookup.model;

import java.util.Objects;

/**
 * Models the mapping between non-normalised and normalised forms of a word from a piece of text to
 * be searched.
 */
public class Word {

    private final String originalText;

    private final int originalStartPosition;

    private final int originalEndPosition;

    private final String lemma;

    /**
     * Constructor.
     *
     * @param originalText the original non-normalised form of the word
     * @param originalStartPosition the start index of the word within the full non-normalised text
     * @param originalEndPosition the end index (exclusive) of the word within the full
     *     non-normalised text
     * @param lemma the lemmatised form of the word if lemmatisation is supported for the relevant
     *     language; the tokenised form if not
     */
    public Word(
            String originalText, int originalStartPosition, int originalEndPosition, String lemma) {
        this.originalText = originalText;
        this.originalStartPosition = originalStartPosition;
        this.originalEndPosition = originalEndPosition;
        this.lemma = lemma;
    }

    public String getOriginalText() {
        return originalText;
    }

    public int getOriginalStartPosition() {
        return originalStartPosition;
    }

    public int getOriginalEndPosition() {
        return originalEndPosition;
    }

    public String getLemma() {
        return lemma;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Word that = (Word) o;
        return originalStartPosition == that.originalStartPosition
                && originalEndPosition == that.originalEndPosition
                && Objects.equals(originalText, that.originalText)
                && Objects.equals(lemma, that.lemma);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalText, originalStartPosition, originalEndPosition, lemma);
    }
}
