package com.digirati.taxman.rest.server.infrastructure.diagnostics;

import org.springframework.dao.EmptyResultDataAccessException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EmptyResultExceptionMapper implements ExceptionMapper<EmptyResultDataAccessException> {

    @Override
    public Response toResponse(EmptyResultDataAccessException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
