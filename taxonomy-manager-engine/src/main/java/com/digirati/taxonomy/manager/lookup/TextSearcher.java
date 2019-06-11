package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.TermMatch;

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
}
