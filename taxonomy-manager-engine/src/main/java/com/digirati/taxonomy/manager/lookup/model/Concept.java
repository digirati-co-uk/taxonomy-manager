package com.digirati.taxonomy.manager.lookup.model;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents some arbitrary concept, including any synonyms for it. Note that heteronyms may appear
 * across multiple different concepts - for example "row" is a synonym for the concept of a fight,
 * the concept of a horizontal line, and is also itself a concept of a way of making boats move. As
 * such, the string "row" could appear as a label for each of those concepts.
 */
public class Concept {

    private final String primaryLabel;

    private final Collection<String> alternateLabels;

    public Concept(String primaryLabel, Collection<String> alternateLabels) {
        this.primaryLabel = primaryLabel;
        this.alternateLabels = alternateLabels;
    }

    public String getPrimaryLabel() {
        return primaryLabel;
    }

    public Collection<String> getAlternateLabels() {
        return alternateLabels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Concept concept = (Concept) o;
        return Objects.equals(primaryLabel, concept.primaryLabel)
                && Objects.equals(alternateLabels, concept.alternateLabels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryLabel, alternateLabels);
    }
}
