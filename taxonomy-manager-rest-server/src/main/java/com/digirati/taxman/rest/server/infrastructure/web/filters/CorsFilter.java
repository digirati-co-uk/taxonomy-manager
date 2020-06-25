package com.digirati.taxman.rest.server.infrastructure.web.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // If running locally, allow CORS to make local dev easier
        if (requestContext.getUriInfo().getBaseUri().getHost().equalsIgnoreCase("localhost")) {
            responseContext.getHeaders().add(
                    "Access-Control-Allow-Origin", "*");
            responseContext.getHeaders().add(
                    "Access-Control-Allow-Headers", "*");
            responseContext.getHeaders().add(
                    "Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }
}
