package com.digirati.taxman.rest.server.infrastructure.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ProjectAlreadyExistsException extends WebApplicationException {

    public ProjectAlreadyExistsException(String slug) {
        super("Project with identifier " + slug + " already exists.", Response.Status.CONFLICT);
    }
}
