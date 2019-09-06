package com.digirati.taxman.analysis;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WordToken {

    private final int offset;
    private final Map<AnnotationType, String> lexemes;

    public WordToken(int offset, Map<AnnotationType, String> lexemes) {
        this.offset = offset;
        this.lexemes = lexemes;
    }

    public static boolean like(List<WordToken> a, List<WordToken> b) {
        if (a.isEmpty() || b.isEmpty()) {
            throw new IllegalArgumentException("Cannot compare empty lists of tokens");
        }

        final List<WordToken> iterator;
        final List<WordToken> cmp;

        if (a.size() > b.size()) {
            iterator = a;
            cmp = b;
        } else {
            iterator = b;
            cmp = a;
        }

        for (int index = 0; index < cmp.size(); index++) {
            var source = iterator.get(index);
            var other = cmp.get(index);

            if (!source.like(other)) {
                return false;
            }
        }

        return true;
    }

    public ImmutableSet<String> candidates() {
        return ImmutableSet.copyOf(lexemes.values());
    }

    public int offset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WordToken wordToken = (WordToken) o;
        return offset == wordToken.offset
                && Objects.equal(lexemes, wordToken.lexemes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(offset, lexemes);
    }

    /**
     * Check if this token is {@code like} another token, i.e., it shares one or more string candidate values.
     *
     * @param token The token to compare against.
     * @return {@code true} iff this token has any candidates in common with the other token.
     */
    public boolean like(WordToken token) {
        return !Collections.disjoint(candidates(), token.candidates());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("offset", offset)
                .add("lexemes", lexemes)
                .toString();
    }
}
