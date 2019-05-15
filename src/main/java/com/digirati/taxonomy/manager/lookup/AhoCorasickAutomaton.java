package com.digirati.taxonomy.manager.lookup;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.Collection;

/**
 * Wrapper around an implementation of the Aho-Corasick algorithm. The algorithm uses a Trie of
 * terms to look for in an input piece of text, and an Automaton/State-machine to traverse that
 * trie, resulting in an efficient multi-string search algorithm.
 */
class AhoCorasickAutomaton {

    private final Trie trie;

    AhoCorasickAutomaton(String... terms) {
        this.trie = initialiseTrieBuilder().addKeywords(terms).build();
    }

    private Trie.TrieBuilder initialiseTrieBuilder() {
        return Trie.builder().ignoreCase().onlyWholeWordsWhiteSpaceSeparated();
    }

    Collection<Emit> search(String inputText) {
        return trie.parseText(inputText);
    }
}
