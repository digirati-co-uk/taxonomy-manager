package com.digirati.taxonomy.manager.lookup.model;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a pairing between the details of where a term was found in a piece of input text, and
 * what that term could mean.
 */
public class ConceptMatch {

    private final TermMatch termMatch;

    private final Collection<UUID> conceptIds;

    public ConceptMatch(TermMatch termMatch, Collection<UUID> conceptIds) {
        this.termMatch = termMatch;
        this.conceptIds = conceptIds;
    }

    public TermMatch getTermMatch() {
        return termMatch;
    }

    public Collection<UUID> getConceptIds() {
        return conceptIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptMatch conceptMatch = (ConceptMatch) o;
        return Objects.equals(termMatch, conceptMatch.termMatch)
                && Objects.equals(conceptIds, conceptMatch.conceptIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termMatch, conceptIds);
    }
}
