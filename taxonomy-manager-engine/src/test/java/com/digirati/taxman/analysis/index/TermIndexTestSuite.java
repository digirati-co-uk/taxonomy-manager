package com.digirati.taxman.analysis.index;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TermIndexTestSuite {
    protected abstract TermIndex<String> create();

    @Test
    public void search_StopsAtSentenceBoundary() {
        var index = create();
        index.add("id1", "finished steel");

        assertEquals(Set.of(), index.search("a sentence is finished. steel."));
    }

    @Test
    public void search_PrefersLongerTerms() {
        var index = create();
        index.add("id1", "finished steel");
        index.add("id2", "steel");

        assertEquals(Set.of("id1"), index.search("finished steel"));
    }
}
