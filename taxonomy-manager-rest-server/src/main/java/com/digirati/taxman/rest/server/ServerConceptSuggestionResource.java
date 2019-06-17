package com.digirati.taxman.rest.server;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

@ApplicationScoped
@Path("/v0.1/concept-scheme/suggestions")
public class ServerConceptSuggestionResource {

    @GET
    public List<String> getSuggestions() {
        return List.of();
    }
}
