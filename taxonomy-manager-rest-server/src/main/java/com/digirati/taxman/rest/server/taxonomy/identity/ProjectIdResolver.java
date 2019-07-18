package com.digirati.taxman.rest.server.taxonomy.identity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@ApplicationScoped
public class ProjectIdResolver {

    @Inject
    UriInfo uriInfo;

    /**
     * Gets the URI of a project with a given slug.
     *
     * @param projectSlug the slug of the project to get the URI for
     * @return the URI of the project with the given slug
     */
    public URI resolve(String projectSlug) {
        URI uri = uriInfo.getRequestUri();
        return UriBuilder.fromUri("/v0.1/project/{slug}")
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .resolveTemplate("slug", projectSlug)
                .build();
    }
}
