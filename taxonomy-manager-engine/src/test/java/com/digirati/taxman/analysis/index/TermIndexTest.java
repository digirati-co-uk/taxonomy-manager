package com.digirati.taxman.analysis.index;

import com.digirati.taxman.analysis.TermMatch;
import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordTokenizer;
import com.digirati.taxman.analysis.search.NaiveSearchStrategy;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TermIndexTest {
    private static final UUID DUMMY_PROJECT_ID = UUID.fromString("3828f4e5-ad0d-402c-978a-e2b9939332c7");

    protected TermIndex<UUID, String> create() {
        return new TermIndex<>(CoreNlpWordTokenizer.create("en"), new NaiveSearchStrategy<>());
    }

    public static void assertTokenIdMatched(Set<String> expectedTokenIds, Set<TermMatch<String>> matches) {
        Set<String> matchedIds = matches
                .stream()
                .map(TermMatch::getId)
                .collect(Collectors.toSet());

        assertEquals(expectedTokenIds, matchedIds);
    }

    @Test
    public void search_ShouldNotIdentifyAcronymsWithDifferentCase() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "CAN");

        assertTokenIdMatched(Set.of(), index.match("I can confirm the topic of today"));
    }

    @Test
    public void search_ShouldIdentifyAcronyms() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "CAN");

        assertTokenIdMatched(Set.of("id1"), index.match("The topic of today is CAN"));
    }

    @Test
    public void search_ShouldFindHyphenDelimitedTokens() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "UAN-30");

        assertTokenIdMatched(Set.of("id1"), index.match("UAN 30%"));
    }

    @Test
    public void search_ShouldFindSlashDelimitedConcepts() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "Ammonium Nitrate");
        index.add(DUMMY_PROJECT_ID, "id2", "CAN");

        assertTokenIdMatched(Set.of("id1", "id2"), index.match("Ammonium Nitrate/CAN"));
    }

    @Test
    public void search_SupportsDuplicateValues() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "finished steel");
        index.add(DUMMY_PROJECT_ID, "id2", "finished steel");

        assertTokenIdMatched(Set.of("id1", "id2"), index.match("finished steel"));
    }

//    @Test
    public void search_DoesntGreedyMatch() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "steel");
        index.add(DUMMY_PROJECT_ID, "id2", "steel girder");

        assertTokenIdMatched(Set.of("id1"), index.match("steel"));
    }

    @Test
    public void search_StopsAtSentenceBoundary() {
        var index = create();
        index.add(DUMMY_PROJECT_ID, "id1", "finished steel");

        assertTokenIdMatched(Set.of(), index.match("a sentence is finished. steel."));
    }

}
