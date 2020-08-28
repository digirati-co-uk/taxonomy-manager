package com.digirati.taxman.rest.analysis;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.rest.MediaTypes;
import com.digirati.taxman.rest.Roles;
import com.digirati.taxman.rest.taxonomy.ConceptSchemePath;
import com.digirati.taxman.rest.taxonomy.ProjectPath;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v0.1")
public interface TextAnalysisResource {

    @POST
    @Path("/analysis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response analyze(@Valid TextAnalysisInput input);

    @POST
    @Path("/{project}/analysis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response analyze(@BeanParam ProjectPath projectPath, @Valid TextAnalysisInput input);

    @POST
    @Path("/{project}/{scheme}/analysis")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response analyze(@BeanParam ConceptSchemePath schemePath, @Valid TextAnalysisInput input);
}
