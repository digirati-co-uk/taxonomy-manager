package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Concept;
import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Encapsulates all operations related to determining which concepts exist within a given piece of
 * text.
 */
public class ConceptLookupHandler {

    private final TextSearcher textSearcher;

    private final Multimap<String, Concept> conceptLookupTable;

    public ConceptLookupHandler(TextSearcher textSearcher) {
        this(textSearcher, ArrayListMultimap.create());
    }

    public ConceptLookupHandler(TextSearcher textSearcher, Multimap<String, Concept> conceptLookupTable) {
        this.textSearcher = textSearcher;
        this.conceptLookupTable = conceptLookupTable;
    }

    public Multimap<String, Concept> getConceptLookupTable() {
        return conceptLookupTable;
    }

    /**
     * Uses the {@link AhoCorasickTextSearcher} to locate any matching terms in the input text, and
     * retrieves any {@link com.digirati.taxonomy.manager.lookup.model.Concept}s that these terms
     * could represent.
     *
     * @param inputText the text from which to extract concepts
     * @return a collection of {@link ConceptMatch}es containing details of what terms were found,
     *     where they were found, and what concepts these terms could be representing
     */
    public Collection<ConceptMatch> search(String inputText) {
        return textSearcher
                .search(inputText)
                .map(this::toConceptMatch)
                .collect(Collectors.toList());
    }

    private ConceptMatch toConceptMatch(TermMatch termMatch) {
        return new ConceptMatch(termMatch, conceptLookupTable.get(termMatch.getTerm()));
    }
}
