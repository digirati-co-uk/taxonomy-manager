package com.digirati.taxman.rest.server.infrastructure.web;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class WebContextListener implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    WebContextHolder contextHolder;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        contextHolder.setUriInfo(requestContext.getUriInfo());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        contextHolder.clear();
    }
}
