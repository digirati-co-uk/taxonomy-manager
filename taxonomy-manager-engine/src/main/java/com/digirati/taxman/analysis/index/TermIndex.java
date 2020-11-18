package com.digirati.taxman.analysis.index;

import com.digirati.taxman.analysis.TermMatch;
import com.digirati.taxman.analysis.WordTokenSearchEntry;
import com.digirati.taxman.analysis.WordTokenSearchStrategy;
import com.digirati.taxman.analysis.WordTokenizer;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A naive {@code O(N * M * 3)} string search algorithm that performs text normalization on stored terms
 * and input queries.
 */
public class TermIndex<ScopeT, IdT>  {

    private final WordTokenizer tokenizer;
    private final WordTokenSearchStrategy<IdT> searchStrategy;
    private final Multimap<ScopeT, IdT> scopedIds = MultimapBuilder.hashKeys().hashSetValues().build();

    public TermIndex(WordTokenizer tokenizer, WordTokenSearchStrategy<IdT> searchStrategy) {
        this.tokenizer = tokenizer;
        this.searchStrategy = searchStrategy;
    }

    public void addAll(ScopeT scope, Map<IdT, String> terms) {
        terms.forEach((id, term) -> add(scope, id, term));
        scopedIds.putAll(scope, terms.keySet());
    }

    public void add(ScopeT scope, IdT id, String text) {
        var tokens = tokenizer.tokenize(text);
        var entry = new WordTokenSearchEntry<>(id, tokens);

        scopedIds.put(scope, id);
        searchStrategy.index(entry);
    }

    public void remove(IdT id, String text) {
        var tokens = tokenizer.tokenize(text);
        var entry = new WordTokenSearchEntry<>(id, tokens);

        searchStrategy.unindex(entry);
    }

    public Set<TermMatch<IdT>> match(ScopeT scope, String input) {
        return match(input)
                .stream()
                .filter(term -> scopedIds.containsEntry(scope, term.getId()))
                .collect(Collectors.toSet());
    }

    public Set<TermMatch<IdT>> match(String input) {
        var tokens = tokenizer.tokenize(input);
        var matches = searchStrategy.match(tokens);

        return matches;
    }
}
