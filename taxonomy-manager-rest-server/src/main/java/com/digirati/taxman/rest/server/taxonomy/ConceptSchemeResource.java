package com.digirati.taxman.rest.server.taxonomy;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/v0.1/concept-scheme")
public class ConceptSchemeResource {

    @GET
    public String test() {
        return "test";
    }

}
