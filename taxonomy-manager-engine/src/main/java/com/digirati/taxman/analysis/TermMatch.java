package com.digirati.taxman.analysis;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

public class TermMatch<IdT> {
    private final IdT id;
    private final List<WordToken> tokens;

    public TermMatch(IdT id, List<WordToken> tokens) {
        this.id = id;
        this.tokens = tokens;
    }

    public IdT getId() {
        return id;
    }

    public int getBeginPosition() {
        return tokens.iterator().next().getBeginPosition();
    }

    public int getEndPosition() {
        return Iterables.getLast(tokens).getEndPosition();
    }

    public List<WordToken> getTokens() {
        return new ArrayList<>(tokens);
    }
}
