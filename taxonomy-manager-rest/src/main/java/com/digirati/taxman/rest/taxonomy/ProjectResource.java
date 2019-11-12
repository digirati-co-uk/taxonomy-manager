package com.digirati.taxman.rest.taxonomy;

import com.digirati.taxman.common.rdf.annotation.jsonld.JsonLdFrame;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.MediaTypes;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Response;

@Path("/v0.1/project")
public interface ProjectResource {

    @GET
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/collection.json")
    Response listProjects();

    @POST
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @Produces({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    @JsonLdFrame(input = "jsonld/framing/project.json")
    Response createProject(@Valid ProjectModel project);

    @GET
    @Path("/{project}")
    @Produces({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    @JsonLdFrame(input = "jsonld/framing/project.json")
    Response getProject(@BeanParam ProjectPath projectPath);

    @PUT
    @Path("/{project}")
    @Consumes({MediaTypes.APPLICATION_RDF_XML_VALUE, MediaTypes.APPLICATION_JSONLD_SKOS_VALUE})
    Response updateProject(@BeanParam ProjectPath projectPath, @Valid ProjectModel project);

    @DELETE
    @Path("/{project}")
    Response deleteProject(@BeanParam ProjectPath projectPath);
}
