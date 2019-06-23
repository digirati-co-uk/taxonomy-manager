package com.digirati.taxman.rest.server.taxonomy.identity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

@RequestScoped
public class ConceptIdResolver extends AbstractIdResolver {

    @Inject
    UriInfo uriInfo;

    ConceptIdResolver() {
        super("/v0.1/concept/:id:");
    }

    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
    }
}
