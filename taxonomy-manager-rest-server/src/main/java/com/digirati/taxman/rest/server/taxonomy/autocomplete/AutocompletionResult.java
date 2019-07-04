package com.digirati.taxman.rest.server.taxonomy.autocomplete;

import java.util.List;

/**
 * Models the entire set of results when attempting to autocomplete a concept label.
 */
public class AutocompletionResult {

    private final String searchTerm;

    private final List<AutocompleteSuggestion> autocompleteSuggestions;

    AutocompletionResult(String searchTerm, List<AutocompleteSuggestion> autocompleteSuggestions) {
        this.searchTerm = searchTerm;
        this.autocompleteSuggestions = autocompleteSuggestions;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public List<AutocompleteSuggestion> getAutocompleteSuggestions() {
        return autocompleteSuggestions;
    }
}
