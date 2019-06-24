package com.digirati.taxman.rest.server.infrastructure.web;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * A JAX-RS request listener that binds the URI of the current request to the {@link Thread} that is handlign it.
 */
@Provider
@PreMatching
public class WebContextListener implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    WebContextHolder contextHolder;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        contextHolder.setUriInfo(requestContext.getUriInfo());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        contextHolder.clear();
    }
}
