package com.digirati.taxman.rest.server.infrastructure.diagnostics;

import com.google.common.collect.Iterables;
import javax.json.Json;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class Rfc7807ValidationErrorMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var violations = Json.createArrayBuilder();

        for (var violation : exception.getConstraintViolations()) {
            var property = Iterables.getLast(violation.getPropertyPath()).getName();

            violations.add(
                    Json.createObjectBuilder()
                            .add("property", property)
                            .add("message", violation.getMessage()));
        }

        var problemJson =
                Json.createObjectBuilder()
                        .add("title", "Validation Error")
                        .add("detail", "Request contents failed validation")
                        .add("status", 422)
                        .add("violations", violations)
                        .build();

        return Response.status(422).type("application/problem+json").entity(problemJson).build();
    }
}
