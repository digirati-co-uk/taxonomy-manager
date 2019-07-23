package com.digirati.taxman.analysis;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class EnglishWordTokenizerTestSuite {
    protected abstract WordTokenizer create();

    private List<WordToken> tokenize(String input) {
        return create().tokenize(input);
    }

    @Test
    public void tokenize_WordsWithLemmas() {
        var tokens = tokenize("was good");

        assertEquals(2, tokens.size());
        assertEquals(Set.of("was", "be"), tokens.get(0).candidates());
        assertEquals(Set.of("good"), tokens.get(1).candidates());
    }

    @Test
    public void tokenize_SimpleWordsNoLemmas() {
        var tokens = tokenize("metal welding");

        assertEquals(2, tokens.size());
        assertEquals(Set.of("metal"), tokens.get(0).candidates());
        assertEquals(Set.of("welding"), tokens.get(1).candidates());
    }

    @Test
    public void tokenize_StripsHyphenation() {
        var tokens = tokenize("metal-welding");

        assertEquals(2, tokens.size());
        assertEquals(Set.of("metal"), tokens.get(0).candidates());
        assertEquals(Set.of("welding"), tokens.get(1).candidates());
    }
}
