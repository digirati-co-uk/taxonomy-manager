package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Concept;
import com.google.common.collect.Lists;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class ConceptLookupTableTest {

    @Test
    void putShouldCreateNewListForFirstMappingBetweenLabelAndConcept() {
        // Given
        Concept road = new Concept("road", Lists.newArrayList("street", "path", "lane"));
        ConceptLookupTable underTest = new ConceptLookupTable();

        // When
        underTest.put("road", road);

        // Then
        MatcherAssert.assertThat(underTest.get("road"), Matchers.contains(road));
    }

    @Test
    void putShouldAddToExistingListForSecondMappingBetweenLabelAndConcept() {
        // Given
        Concept line = new Concept("line", Lists.newArrayList("row"));
        Concept fight = new Concept("fight", Lists.newArrayList("row"));
        ConceptLookupTable underTest = new ConceptLookupTable();
        underTest.put("row", line);

        // When
        underTest.put("row", fight);

        // Then
        MatcherAssert.assertThat(underTest.get("row"), Matchers.contains(line, fight));
    }
}
