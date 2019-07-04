package com.digirati.taxman.rest.server.taxonomy.autocomplete;

import com.digirati.taxman.common.taxonomy.ConceptModel;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps a {@link ConceptModel} to an {@link AutocompleteSuggestion}.
 */
class ConceptAutocompleteSuggestionMapper {

    private final String searchTerm;

    ConceptAutocompleteSuggestionMapper(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    AutocompleteSuggestion map(ConceptModel conceptModel) {
        URI uri = conceptModel.getUri();
        Map<String, String> preferredLabel = conceptModel.getPreferredLabel();
        Set<String> matchedLanguages = new HashSet<>();
        for (Map.Entry<String, String> localisedLabel : preferredLabel.entrySet()) {
            if (localisedLabel.getValue().startsWith(searchTerm)) {
                matchedLanguages.add(localisedLabel.getKey());
            }
        }
        return new AutocompleteSuggestion(uri, preferredLabel, matchedLanguages);
    }
}
