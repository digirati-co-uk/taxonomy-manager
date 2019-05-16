package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.Concept;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a lookup table between keyword terms and {@link Concept}s. For example, the String
 * "row" could represent the concept of a horizontal line, a fight, or a way of making a boat move.
 * This gives us a mechanism to go from determining that the input string contains the word "row" to
 * determining candidates for what a "row" actually is.
 */
public class ConceptLookupTable {

    private final Map<String, List<Concept>> labelToConceptMap;

    ConceptLookupTable() {
        this.labelToConceptMap = new HashMap<>();
    }

    /**
     * Associates a String to any {@link Concept}s that that string could be referring to.
     *
     * @param label the String that could signify the concepts
     * @param concepts the {@link Concept}s that could be signified by the String
     */
    public void put(String label, Concept... concepts) {
        if (labelToConceptMap.containsKey(label)) {
            labelToConceptMap.get(label).addAll(Arrays.asList(concepts));
        } else {
            labelToConceptMap.put(label, Lists.newArrayList(concepts));
        }
    }

    /**
     * Retrieves all {@link Concept}s that a given String could be referring to.
     *
     * @param label the String for which to retrieve any associated {@link Concept}s
     * @return all {@link Concept}s associated to the label
     */
    public List<Concept> get(String label) {
        return labelToConceptMap.get(label);
    }
}
