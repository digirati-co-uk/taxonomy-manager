package com.digirati.taxman.rest.taxonomy;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
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
import javax.ws.rs.core.Response;

@Path("/v0.1")
public interface ConceptSchemeResource {
    @POST
    @Path("/{project}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/concept-scheme.json")
    Response createConceptScheme(@BeanParam ProjectPath projectPath, @Valid ConceptSchemeModel model);

    @GET
    @Path("/{project}/{scheme}")
    @Consumes({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/concept-scheme.json")
    Response getConceptScheme(@BeanParam ConceptSchemePath params);

    @PUT
    @Path("/{project}/{scheme}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    Response updateConceptScheme(@BeanParam ConceptSchemePath params, @Valid ConceptSchemeModel model);

    @DELETE
    @Path("/{project}/{scheme}")
    Response deleteConceptScheme(@BeanParam ConceptSchemePath params);
}
