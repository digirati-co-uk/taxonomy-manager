package com.digirati.taxman.rest.taxonomy;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.MediaTypes;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/v0.1/concept")
public interface ConceptResource {
    @POST
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/concept.json")
    Response createConcept(@Valid ConceptModel model);

    @GET
    @Path("/{concept}")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/concept.json")
    Response getConcept(@BeanParam ConceptPath params);

    @GET
    @Path("/{concept}/relationships")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response getRelationships(@BeanParam ConceptPath params,
                              @Valid @BeanParam ConceptRelationshipParams relationshipParams);

    @GET
    @Path("/search")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response getConceptsByPartialLabel(@HeaderParam("X-Project-Slug") String projectSlug,
                                       @QueryParam("label") String partialLabel,
                                       @QueryParam("language") String languageKey,
                                       @QueryParam("filter") String filter);

    @PUT
    @Path("/{concept}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    Response updateConcept(@BeanParam ConceptPath params, @Valid ConceptModel model);

    @DELETE
    @Path("/{concept}")
    Response deleteConcept(@BeanParam ConceptPath params);
}
