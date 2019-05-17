package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import org.ahocorasick.trie.Trie;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Wrapper around an implementation of the Aho-Corasick algorithm. The algorithm uses a Trie of
 * terms to look for in an input piece of text, and an Automaton/State-machine to traverse that
 * trie, resulting in an efficient multi-string search algorithm.
 */
class AhoCorasickTextSearcher implements TextSearcher {

    private final Trie trie;

    AhoCorasickTextSearcher(Collection<String> terms) {
        this.trie = initialiseTrieBuilder().addKeywords(terms).build();
    }

    private Trie.TrieBuilder initialiseTrieBuilder() {
        return Trie.builder().ignoreCase().onlyWholeWordsWhiteSpaceSeparated();
    }

    public Stream<TermMatch> search(String inputText) {
        return trie.parseText(inputText).stream()
                .map(emit -> new TermMatch(emit.getKeyword(), emit.getStart(), emit.getEnd()));
    }
}
