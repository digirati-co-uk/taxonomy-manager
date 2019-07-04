package com.digirati.taxman.rest.server.taxonomy.autocomplete;

import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * A single suggestion to be supplied when attempting to autocomplete a label corresponding to a concept.
 */
public class AutocompleteSuggestion {

    private final URI uri;

    private final Map<String, String> preferredLabel;

    private final Set<String> matchedLanguages;

    AutocompleteSuggestion(URI uri, Map<String, String> preferredLabel, Set<String> matchedLanguages) {
        this.uri = uri;
        this.preferredLabel = preferredLabel;
        this.matchedLanguages = matchedLanguages;
    }

    public URI getUri() {
        return uri;
    }

    public Map<String, String> getPreferredLabel() {
        return preferredLabel;
    }

    public Set<String> getMatchedLanguages() {
        return matchedLanguages;
    }
}
