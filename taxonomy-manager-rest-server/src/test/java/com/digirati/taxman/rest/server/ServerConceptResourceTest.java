package com.digirati.taxman.rest.server;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static com.digirati.taxman.rest.server.testing.util.RestAssuredUtils.givenJsonLdRequest;

@QuarkusTest
public class ServerConceptResourceTest {

    @Test
    public void createConcept_JsonLd() {
        // @formatter:off
        givenJsonLdRequest(getClass(), "concept--create.json")
                .when()
                    .post("/v0.1/concept")
                .then()
                    .statusCode(201);
        // @formatter:on
    }

    @Test
    public void createConcept_JsonLdFailsValidationNoPrefLabel() {
        // @formatter:off
        givenJsonLdRequest(getClass(), "concept--create-no-pref-label.json")
                .when()
                    .post("/v0.1/concept")
                .then()
                    .statusCode(422);
        // @formatter:on
    }

}
