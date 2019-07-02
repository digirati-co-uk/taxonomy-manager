package com.digirati.taxman.common.taxonomy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A representation of a SKOS concept.
 */
public interface Concept {

    UUID getUuid();

    Map<String, String> getPreferredLabel();

    Map<String, String> getAltLabel();

    Map<String, String> getHiddenLabel();

    Map<String, String> getNote();

    Map<String, String> getChangeNote();

    Map<String, String> getEditorialNote();

    Map<String, String> getExample();

    Map<String, String> getHistoryNote();

    Map<String, String> getScopeNote();

    /**
     * Gets all labels for this concept for a given language key.
     *
     * @param languageKey the language key of the labels to get
     * @return a set of all labels describing this concept for the given language
     */
    default Stream<String> getLabels(String languageKey) {
        Set<String> labels = new HashSet<>();
        if (getPreferredLabel().containsKey(languageKey)) {
            labels.add(getPreferredLabel().get(languageKey));
        }
        if (getAltLabel().containsKey(languageKey)) {
            labels.add(getAltLabel().get(languageKey));
        }
        if (getHiddenLabel().containsKey(languageKey)) {
            labels.add(getHiddenLabel().get(languageKey));
        }
        return labels.stream();
    }
}
