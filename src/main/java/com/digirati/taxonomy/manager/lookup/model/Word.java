package com.digirati.taxonomy.manager.lookup.model;

import edu.stanford.nlp.ling.CoreLabel;

import java.util.Objects;

/**
 * Models the mapping between non-normalised and normalised forms of a word from a piece of text to
 * be searched.
 */
public class Word {

    private String originalText;

    private int originalStartPosition;

    private int originalEndPosition;

    private String lemma;

    public Word(
            String originalText, int originalStartPosition, int originalEndPosition, String lemma) {
        this.originalText = originalText;
        this.originalStartPosition = originalStartPosition;
        this.originalEndPosition = originalEndPosition;
        this.lemma = lemma;
    }

    public Word(CoreLabel coreLabel) {
        this(
                coreLabel.originalText(),
                coreLabel.beginPosition(),
                coreLabel.endPosition(),
                coreLabel.lemma());
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
