package com.digirati.taxman.rest.server.infrastructure.web;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.UriInfo;

/**
 * An application wide thread-local context that maintains the active URI for the current request.
 */
@ApplicationScoped
class WebContextHolder {
    private final ThreadLocal<UriInfo> localUri = new ThreadLocal<>();

    void setUriInfo(UriInfo info) {
        localUri.set(info);
    }

    void clear() {
        localUri.remove();
    }

    /**
     * A request scoped bean that returns the {@link UriInfo} of the request active in the current
     * thread.
     *
     * @return The {@link UriInfo} of the active request.
     */
    @RequestScoped
    @Produces
    UriInfo uriInfo() {
        return localUri.get();
    }
}
