package com.digirati.taxman.common.taxonomy;

import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A representation of a SKOS concept.
 */
public interface Concept {

    UUID getUuid();

    String getSource();

    Multimap<String, String> getPreferredLabel();

    Multimap<String, String> getAltLabel();

    Multimap<String, String> getHiddenLabel();

    Multimap<String, String> getNote();

    Multimap<String, String> getChangeNote();

    Multimap<String, String> getEditorialNote();

    Multimap<String, String> getExample();

    Multimap<String, String> getHistoryNote();

    Multimap<String, String> getScopeNote();

    /**
     * Gets all labels for this concept for a given language key.
     *
     * @param languageKey the language key of the labels to get
     * @return a set of all labels describing this concept for the given language
     */
    default Set<String> getLabels(String languageKey) {
        Set<String> labels = new HashSet<>();
        if (getPreferredLabel().containsKey(languageKey)) {
            labels.addAll(getPreferredLabel().get(languageKey));
        }
        if (getAltLabel().containsKey(languageKey)) {
            labels.addAll(getAltLabel().get(languageKey));
        }
        if (getHiddenLabel().containsKey(languageKey)) {
            labels.addAll(getHiddenLabel().get(languageKey));
        }
        return labels;
    }
}
