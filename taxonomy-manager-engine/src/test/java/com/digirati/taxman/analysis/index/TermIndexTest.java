package com.digirati.taxman.analysis.index;

import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordTokenizer;
import com.digirati.taxman.analysis.search.NaiveSearchStrategy;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TermIndexTest {
    protected TermIndex<String> create() {
        return new TermIndex<>(CoreNlpWordTokenizer.create("en"), new NaiveSearchStrategy<>());
    }

    @Test
    public void search_ShouldFindSlashDelimitedConcepts() {
        var index = create();
        index.add("id1", "Ammonium Nitrate");
        index.add("id2", "CAN");

        assertEquals(Set.of("id1", "id2"), index.match("Ammonium Nitrate/CAN"));
    }

    @Test
    public void search_SupportsDuplicateValues() {
        var index = create();
        index.add("id1", "finished steel");
        index.add("id2", "finished steel");

        assertEquals(Set.of("id1", "id2"), index.match("finished steel"));
    }

//    @Test
    public void search_DoesntGreedyMatch() {
        var index = create();
        index.add("id1", "steel");
        index.add("id2", "steel girder");

        assertEquals(Set.of("id1"), index.match("steel"));
    }

    @Test
    public void search_StopsAtSentenceBoundary() {
        var index = create();
        index.add("id1", "finished steel");

        assertEquals(Set.of(), index.match("a sentence is finished. steel."));
    }

}
