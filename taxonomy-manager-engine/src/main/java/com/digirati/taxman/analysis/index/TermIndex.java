package com.digirati.taxman.analysis.index;

import com.digirati.taxman.analysis.WordTokenSearchEntry;
import com.digirati.taxman.analysis.WordTokenSearchStrategy;
import com.digirati.taxman.analysis.WordTokenizer;

import java.util.Map;
import java.util.Set;

/**
 * A naive {@code O(N * M * 3)} string search algorithm that performs text normalization on stored terms
 * and input queries.
 */
public class TermIndex<IdT>  {

    private final WordTokenizer tokenizer;
    private final WordTokenSearchStrategy<IdT> searchStrategy;

    public TermIndex(WordTokenizer tokenizer, WordTokenSearchStrategy<IdT> searchStrategy) {
        this.tokenizer = tokenizer;
        this.searchStrategy = searchStrategy;
    }

    public void addAll(Map<IdT, String> terms) {
        terms.forEach(this::add);
    }

    public void add(IdT id, String text) {
        var tokens = tokenizer.tokenize(text);
        var entry = new WordTokenSearchEntry<>(id, tokens);

        searchStrategy.index(entry);
    }

    public void remove(IdT id, String text) {
        var tokens = tokenizer.tokenize(text);
        var entry = new WordTokenSearchEntry<>(id, tokens);

        searchStrategy.unindex(entry);
    }

    public Set<IdT> match(String input) {
        var tokens = tokenizer.tokenize(input);
        var matches = searchStrategy.match(tokens);

        return matches;
    }
}
