package com.digirati.taxman.rest.server.taxonomy.concept.autosuggest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@ApplicationScoped
@Path("/v0.1/concept-scheme/suggestions")
public class ConceptSuggestionResource {

    @GET
    public List<String> getSuggestions() {
        return List.of();
    }
}
