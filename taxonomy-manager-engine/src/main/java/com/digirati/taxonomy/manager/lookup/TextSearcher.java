package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.TermMatch;

import java.util.Set;
import java.util.stream.Stream;

public interface TextSearcher {

    /**
     * Searches an input String for occurrences of a given set of terms provided by any mechanism
     * (e.g. a constant list, a list of strings provided at construction time, etc.).
     *
     * @param inputText the text in which to search for the terms
     * @return a stream of {@link TermMatch}es each representing an occurrence of a term in the text
     */
    Stream<TermMatch> search(String inputText);

    /**
     * Rebuilds the text searcher with an updated set of terms to search for. This could be
     * accomplished by updating the internal state of this instance, or by constructing a new
     * instance.
     *
     * @param terms the updated set of terms to search for
     * @return a {@link TextSearcher} with the updated term set loaded into it
     */
    TextSearcher rebuild(Set<String> terms);
}
