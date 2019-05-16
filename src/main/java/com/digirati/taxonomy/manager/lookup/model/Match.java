package com.digirati.taxonomy.manager.lookup.model;

import org.ahocorasick.trie.Emit;

import java.util.List;
import java.util.Objects;

/**
 * Represents a pairing between the details of where a term was found in a piece of input text, and
 * what that term could mean.
 */
public class Match {

    private final Emit emit;

    private final List<Concept> matchingConcepts;

    public Match(Emit emit, List<Concept> matchingConcepts) {
        this.emit = emit;
        this.matchingConcepts = matchingConcepts;
    }

    public Emit getEmit() {
        return emit;
    }

    public List<Concept> getMatchingConcepts() {
        return matchingConcepts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match match = (Match) o;
        return Objects.equals(emit, match.emit)
                && Objects.equals(matchingConcepts, match.matchingConcepts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emit, matchingConcepts);
    }
}
