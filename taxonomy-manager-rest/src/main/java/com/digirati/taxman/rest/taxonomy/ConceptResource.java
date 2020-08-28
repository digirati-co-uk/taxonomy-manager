package com.digirati.taxman.rest.taxonomy;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.MediaTypes;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/v0.1")
public interface ConceptResource {
    @POST
    @Path("/{project}/{scheme}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/concept.json")
    Response createConcept(@BeanParam ConceptSchemePath params, @Valid ConceptModel model);

    @GET
    @Path("/{project}/{scheme}/{concept}")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/concept.json")
    Response getConcept(@BeanParam ConceptPath params);

    @GET
    @Path("/{project}/{scheme}/{concept}/relationships")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response getRelationships(@BeanParam ConceptPath params,
                              @Valid @BeanParam ConceptRelationshipParams relationshipParams);

    @GET
    @Path("/search")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response getConceptsByPartialLabel(@QueryParam("label") String partialLabel,
                                       @QueryParam("language") String languageKey,
                                       @QueryParam("scheme") UUID conceptSchemeUuid,
                                       @QueryParam("project") String projectSlug
                                       );

    @PUT
    @Path("/{project}/{scheme}/{concept}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    Response updateConcept(@BeanParam ConceptPath params, @Valid ConceptModel model);

    @DELETE
    @Path("/{project}/{scheme}/{concept}")
    Response deleteConcept(@BeanParam ConceptPath params);
}
