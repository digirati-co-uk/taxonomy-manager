package com.digirati.taxonomy.manager.lookup.model;

import java.util.Objects;

/**
 * Represents an occurrence of a term in a piece of input text, including what term was found and
 * where it was found.
 */
public class TermMatch {

    private final String term;

    private final int startIndex;

    private final int endIndex;

    /**
     * Constructs a {@link TermMatch} containing details of what term was found and where.
     *
     * @param term the term that was found.
     * @param startIndex the index of the first character of the term match in the original text
     * @param endIndex the index of the last character of the term match in the original text
     */
    public TermMatch(String term, int startIndex, int endIndex) {
        this.term = term;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public String getTerm() {
        return term;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TermMatch termMatch = (TermMatch) o;
        return startIndex == termMatch.startIndex
                && endIndex == termMatch.endIndex
                && Objects.equals(term, termMatch.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, startIndex, endIndex);
    }
}
