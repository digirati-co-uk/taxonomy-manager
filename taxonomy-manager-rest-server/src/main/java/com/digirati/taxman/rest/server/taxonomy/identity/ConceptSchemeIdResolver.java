package com.digirati.taxman.rest.server.taxonomy.identity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

@ApplicationScoped
public class ConceptSchemeIdResolver extends AbstractIdResolver {

    @Inject
    UriInfo uriInfo;

    ConceptSchemeIdResolver() {
        super("/v0.1/concept-scheme/:id:");
    }

    @Override
    protected UriInfo getUriInfo() {
        return uriInfo;
    }
}
