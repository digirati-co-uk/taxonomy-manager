package com.digirati.taxman.analysis;

import com.digirati.taxman.analysis.nlp.AnnotationType;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WordToken {

    private final Map<AnnotationType, String> lexemes;
    private final int beginPosition;
    private final int endPosition;

    public WordToken(Map<AnnotationType, String> lexemes, int beginPosition, int endPosition) {
        this.lexemes = lexemes;
        this.beginPosition = beginPosition;
        this.endPosition = endPosition;
    }

    public static boolean sharesCandidates(List<WordToken> a, List<WordToken> b) {
        if (a.isEmpty() || b.isEmpty()) {
            throw new IllegalArgumentException("Cannot compare empty lists of tokens");
        } else if (a.size() != b.size()) {
            return false;
        }

        for (int index = 0; index < a.size(); index++) {
            var source = a.get(index);
            var other = b.get(index);

            if (!source.sharesCandidates(other)) {
                return false;
            }
        }

        return true;
    }

    public Collection<String> candidates() {
        return lexemes.values();
    }

    public int getBeginPosition() {
        return beginPosition;
    }

    public int getEndPosition() {
        return endPosition;
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
        return Objects.equal(lexemes, wordToken.lexemes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lexemes);
    }

    /**
     * Check if this token is {@code like} another token, i.e., it shares one or more string candidate values.
     *
     * @param token The token to compare against.
     * @return {@code true} iff this token has any candidates in common with the other token.
     */
    public boolean sharesCandidates(WordToken token) {
        Collection<String> candidates = candidates();

        for (String candidate : token.candidates()) {
            if (candidates.contains(candidate)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lexemes", lexemes)
                .toString();
    }
}
