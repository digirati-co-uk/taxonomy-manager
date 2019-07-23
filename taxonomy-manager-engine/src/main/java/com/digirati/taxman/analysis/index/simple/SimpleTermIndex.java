package com.digirati.taxman.analysis.index.simple;

import com.digirati.taxman.analysis.WordToken;
import com.digirati.taxman.analysis.WordTokenizer;
import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.analysis.index.TermIndexEntry;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * A naive {@code O(N * M * 3)} string search algorithm that performs text normalization on stored terms
 * and input queries.
 */
public class SimpleTermIndex<IdT> implements TermIndex<IdT> {

    private static final Comparator<TermIndexEntry> ENTRY_SIZE_COMPARATOR =
            Comparator.comparing(entry -> entry.getTokens().size());

    private final WordTokenizer tokenizer;
    private final List<TermIndexEntry<IdT>> entries = new CopyOnWriteArrayList<>();

    public SimpleTermIndex(WordTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public void addAll(Map<IdT, String> terms) {
        var newEntries = terms.entrySet()
                .stream()
                .map(entry -> {
                    var id = entry.getKey();
                    var tokens = tokenizer.tokenize(entry.getValue());

                    return new TermIndexEntry<>(id, tokens);
                })
                .collect(Collectors.toList());

        entries.addAll(newEntries);
    }

    @Override
    public void add(IdT id, String text) {
        var tokens = tokenizer.tokenize(text);
        var entry = new TermIndexEntry<>(id, tokens);

        entries.add(entry);
    }

    @Override
    public void remove(IdT id) {
        entries.removeIf(entry -> entry.getIdentity().equals(id));
    }

    @Override
    public Set<IdT> search(String input) {
        var matches = new HashSet<IdT>();
        var tokens = tokenizer.tokenize(input);

        // @TODO gtierney: we need a fast contains(List<WordToken>) implementation for List<WordToken>,
        //                 implementing a CharSequence iterator over a custom WordToken collection and relying on
        //                 existing algorithms that work on strings could work
        for (int tokenIndex = 0; tokenIndex < tokens.size(); tokenIndex++) {
            var token = tokens.get(tokenIndex);
            var candidateEntries = entries.parallelStream()
                    .filter(entry -> token.like(entry.getRootToken()))
                    .sorted(ENTRY_SIZE_COMPARATOR.reversed()) // Prefer matches on longer terms first
                    .collect(Collectors.toList());

            for (var candidate : candidateEntries) {
                var candidateTokens = candidate.getTokens();
                var currentTokenEndIndex = Math.min(tokenIndex + candidateTokens.size() + 1, tokens.size());
                var currentTokens = tokens.subList(tokenIndex, currentTokenEndIndex);

                if (WordToken.like(candidateTokens, currentTokens)) {
                    matches.add(candidate.getIdentity());
                    tokenIndex = currentTokenEndIndex;

                    break;
                }
            }
        }

        return matches;
    }
}
