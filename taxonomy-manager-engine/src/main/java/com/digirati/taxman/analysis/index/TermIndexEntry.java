package com.digirati.taxman.analysis.index;

import com.digirati.taxman.analysis.WordToken;
import com.google.common.base.Objects;

import java.util.List;

public class TermIndexEntry<IdT> {
    private final IdT identity;
    private final List<WordToken> tokens;

    public TermIndexEntry(IdT identity, List<WordToken> tokens) {
        this.identity = identity;
        this.tokens = tokens;
    }

    public IdT getIdentity() {
        return identity;
    }

    public List<WordToken> getTokens() {
        return tokens;
    }

    public WordToken getRootToken() {
        if (tokens.isEmpty()) {
            throw new IllegalStateException("A term index entry cannot exist without a root token");
        }

        return tokens.get(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TermIndexEntry<?> that = (TermIndexEntry<?>) o;
        return Objects.equal(identity, that.identity)
                && Objects.equal(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identity, tokens);
    }
}
