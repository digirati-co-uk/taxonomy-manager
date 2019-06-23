package com.digirati.taxman.rest.server.infrastructure.web;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.UriInfo;

@ApplicationScoped
class WebContextHolder {
    private ThreadLocal<UriInfo> localUri = new ThreadLocal<>();

    void setUriInfo(UriInfo info) {
        localUri.set(info);
    }

    void clear() {
        localUri.remove();
    }

    @RequestScoped
    @Produces
    UriInfo uriInfo() {
        return localUri.get();
    }

}
