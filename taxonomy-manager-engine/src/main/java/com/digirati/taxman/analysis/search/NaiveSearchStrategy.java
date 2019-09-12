package com.digirati.taxman.analysis.search;

import com.digirati.taxman.analysis.WordToken;
import com.digirati.taxman.analysis.WordTokenSearchEntry;
import com.digirati.taxman.analysis.WordTokenSearchStrategy;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class NaiveSearchStrategy<IdT> implements WordTokenSearchStrategy<IdT> {
    private final List<WordTokenSearchEntry<IdT>> entries = new CopyOnWriteArrayList<>();

    private static final Comparator<WordTokenSearchEntry> ENTRY_SIZE_COMPARATOR =
            Comparator.comparing(entry -> entry.getTokens().size());

    @Override
    public void index(WordTokenSearchEntry<IdT> entry) {
        entries.add(entry);
    }

    @Override
    public void unindex(WordTokenSearchEntry<IdT> entry) {
        entries.remove(entry);
    }

    @Override
    public Set<IdT> match(List<WordToken> tokens) {
        var matches = new HashSet<IdT>();

        for (int tokenIndex = 0; tokenIndex < tokens.size(); tokenIndex++) {
            var token = tokens.get(tokenIndex);
            var candidateEntries = entries.parallelStream()
                    .filter(entry -> token.sharesCandidates(entry.getRootToken()))
                    .sorted(ENTRY_SIZE_COMPARATOR.reversed()) // Prefer matches on longer terms first
                    .collect(Collectors.toList());

            for (var candidate : candidateEntries) {
                var candidateTokens = candidate.getTokens();
                var currentTokenEndIndex = Math.min(tokenIndex + candidateTokens.size(), tokens.size());
                var currentTokens = tokens.subList(tokenIndex, currentTokenEndIndex);

                if (WordToken.sharesCandidates(candidateTokens, currentTokens)) {
                    matches.add(candidate.getIdentity());
                }
            }
        }

        return matches;
    }
}
