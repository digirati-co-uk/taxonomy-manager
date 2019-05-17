package com.digirati.taxonomy.manager.lookup.model;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a pairing between the details of where a term was found in a piece of input text, and
 * what that term could mean.
 */
public class ConceptMatch {

    private final TermMatch termMatch;

    private final Collection<Concept> concepts;

    public ConceptMatch(TermMatch termMatch, Collection<Concept> concepts) {
        this.termMatch = termMatch;
        this.concepts = concepts;
    }

    public TermMatch getTermMatch() {
        return termMatch;
    }

    public Collection<Concept> getConcepts() {
        return concepts;
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
                && Objects.equals(concepts, conceptMatch.concepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termMatch, concepts);
    }
}
