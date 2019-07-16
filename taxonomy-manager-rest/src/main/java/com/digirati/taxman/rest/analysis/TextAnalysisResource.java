package com.digirati.taxman.rest.analysis;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.rest.MediaTypes;
import com.digirati.taxman.rest.Roles;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v0.1/analysis")
public interface TextAnalysisResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    @RolesAllowed(Roles.ADMIN)
    Response analyze(@Valid TextAnalysisInput input);
}
