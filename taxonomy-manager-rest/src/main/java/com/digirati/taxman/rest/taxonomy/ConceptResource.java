package com.digirati.taxman.rest.taxonomy;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.MediaTypes;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v0.1/concept")
public interface ConceptResource {
    @POST
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "/jsonld/framing/concept.json")
    Response createConcept(@Valid ConceptModel model);

    @GET
    @Path("/{concept}")
    @Consumes({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "/jsonld/framing/concept.json")
    Response getConcept(@BeanParam ConceptPath params);

    @GET
    @Path("/by-label/{label}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    Response getConceptsByPartialLabel(@PathParam("label") String partialLabel);

    @PUT
    @Path("/{concept}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    Response updateConcept(@BeanParam ConceptPath params, @Valid ConceptModel model);
}
