package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.ConceptRdfModel;
import com.digirati.taxman.rest.taxonomy.ConceptPath;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
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
public class ServerConceptResource {

    @POST
    @Path("/v0.1/concept-scheme/{scheme}/concept")
    public Response createConcept(@PathParam("scheme") String scheme, @Valid ConceptRdfModel model)
            throws URISyntaxException {
        return Response.created(new URI("http://localhost/test")).build();
    }

    @DELETE
    @Path("/v0.1/concept-scheme/{scheme}/concept/{concept}")
    public Response deleteConcept(@BeanParam ConceptPath params) {
        return Response.noContent().build();
    }

    @GET
    @Path("/v0.1/concept-scheme/{scheme}/concept/{concept}")
    public Response getConcept(@BeanParam ConceptPath params) {
        return Response.status(200).build();
    }

    @PUT
    @Path("/v0.1/concept-scheme/{scheme}/concept/{concept}")
    public Response updateConcept(@BeanParam ConceptPath params) {
        return Response.status(204).build();
    }
}
