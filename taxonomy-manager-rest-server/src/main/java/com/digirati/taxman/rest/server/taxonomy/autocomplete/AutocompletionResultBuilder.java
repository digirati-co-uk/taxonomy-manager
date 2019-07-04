package com.digirati.taxman.rest.server.taxonomy.autocomplete;

import com.digirati.taxman.common.taxonomy.ConceptModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds an {@link AutocompletionResult} from its constituent parts.
 */
public class AutocompletionResultBuilder {

    private String searchTerm;

    private ConceptAutocompleteSuggestionMapper conceptAutocompleteSuggestionMapper;

    private Collection<ConceptModel> matchedConcepts;

    public AutocompletionResultBuilder withSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        this.conceptAutocompleteSuggestionMapper = new ConceptAutocompleteSuggestionMapper(searchTerm);
        return this;
    }

    public AutocompletionResultBuilder withMatchedConcepts(Collection<ConceptModel> matchedConcepts) {
        this.matchedConcepts = matchedConcepts;
        return this;
    }

    public AutocompletionResult build() {
        List<AutocompleteSuggestion> autocompleteSuggestions = matchedConcepts.stream()
                .map(conceptAutocompleteSuggestionMapper::map)
                .collect(Collectors.toList());
        return new AutocompletionResult(searchTerm, autocompleteSuggestions);
    }
}
