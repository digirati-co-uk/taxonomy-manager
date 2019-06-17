package com.digirati.taxman.rest.server;

import com.digirati.taxman.rest.MediaTypes;
import org.apache.jena.rdf.model.Model;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/v0.1/concept-scheme")
public class ServerConceptSchemeResource {

    @POST
    @Consumes({MediaTypes.APPLICATION_JSONLD_SKOS_VALUE, MediaTypes.APPLICATION_RDF_XML_VALUE})
    public void createConceptSchemeFromJsonLd(Model model) {}
}
