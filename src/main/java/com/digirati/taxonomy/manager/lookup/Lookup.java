package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Match;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Encapsulates all operations related to determining which concepts exist within a given piece of
 * text.
 */
public class Lookup {

    private AhoCorasickAutomaton ahoCorasickAutomaton;

    private ConceptLookupTable conceptLookupTable;

    /**
     * Constructor creates all data structures necessary for extracting {@link
     * com.digirati.taxonomy.manager.lookup.model.Concept}s from a given piece of text. At present
     * this consists of an {@link AhoCorasickAutomaton} which is used to locate terms in text, and a
     * {@link ConceptLookupTable} which is used to convert term matches into {@link
     * com.digirati.taxonomy.manager.lookup.model.Concept} matches.
     *
     * @param terms all possible terms to look for
     */
    public Lookup(String... terms) {
        ahoCorasickAutomaton = new AhoCorasickAutomaton(terms);
        conceptLookupTable = new ConceptLookupTable();
    }

    public ConceptLookupTable getConceptLookupTable() {
        return conceptLookupTable;
    }

    /**
     * Uses the {@link AhoCorasickAutomaton} to locate any matching terms in the input text, and
     * retrieves any {@link com.digirati.taxonomy.manager.lookup.model.Concept}s that these terms
     * could represent.
     *
     * @param inputText the text from which to extract concepts
     * @return a collection of {@link Match}es containing details of what terms were found, where
     *     they were found, and what concepts these terms could be representing
     */
    public Collection<Match> search(String inputText) {
        return ahoCorasickAutomaton.search(inputText).stream()
                .map(emit -> new Match(emit, conceptLookupTable.get(emit.getKeyword())))
                .collect(Collectors.toList());
    }

    /**
     * Replaces the current {@link AhoCorasickAutomaton} with a new one. When changes are made to
     * what terms should be listed in it, it isn't possible to update the existing automaton with
     * those changes without a lot of very error-prone work - instead we simply rebuild it from
     * scratch
     *
     * @param terms the terms to pass into the reconstructed automaton
     */
    public void rebuildAutomaton(String... terms) {
        ahoCorasickAutomaton = new AhoCorasickAutomaton(terms);
    }

    /**
     * Replaces the current {@link ConceptLookupTable} with an empty new one. When changes need to
     * be made to the structure of terms and concepts, updating the existing lookup table is likely
     * to be error prone - instead we simply rebuild it from scratch
     */
    public void clearConceptLookupTable() {
        conceptLookupTable = new ConceptLookupTable();
    }
}
