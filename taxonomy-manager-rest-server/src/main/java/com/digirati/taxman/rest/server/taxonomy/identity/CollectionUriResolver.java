package com.digirati.taxman.rest.server.taxonomy.identity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@RequestScoped
public class CollectionUriResolver {

    @Inject
    UriInfo uriInfo;

    public URI resolve() {
        return uriInfo.getRequestUri();
    }
}
