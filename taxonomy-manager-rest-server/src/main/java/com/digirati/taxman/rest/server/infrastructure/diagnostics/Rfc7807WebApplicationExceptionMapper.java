package com.digirati.taxman.rest.server.infrastructure.diagnostics;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class Rfc7807WebApplicationExceptionMapper
        implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        var response = exception.getResponse();
        var type = response.getStatusInfo();

        var problemJson =
                Json.createObjectBuilder()
                        .add("title", type.getFamily().toString())
                        .add("detail", type.getReasonPhrase())
                        .add("status", type.getStatusCode())
                        .build();

        return Response.status(type.getStatusCode())
                .type("application/problem+json")
                .entity(problemJson)
                .build();
    }
}
