package com.digirati.taxman.rest.server.taxonomy.concept;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
@Path("/v0.1/concept-scheme/{scheme}/concept")
public class ConceptResource {

    @POST
    public Response createConcept(@PathParam("scheme") String scheme) throws URISyntaxException {
        return Response.created(new URI("http://localhost/test")).build();
    }

    @DELETE
    @Path("/{concept}")
    public Response deleteConcept(@BeanParam ConceptParams params) {
        return Response.noContent().build();
    }

    @GET
    @Path("/{concept}")
    public Response getConcept(@BeanParam ConceptParams params) {
        return Response.status(200).build();
    }

    @PUT
    @Path("/{concept}")
    public Response updateConcept(@BeanParam ConceptParams params) {
        return Response.status(204).build();
    }
}
