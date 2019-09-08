package com.digirati.taxman.rest.server;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import static com.digirati.taxman.rest.server.testing.util.RestAssuredUtils.givenJsonLdRequest;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
@Transactional
public class ServerConceptSchemeResourceTest {
    @Test
    public void createConceptScheme_WithTopConcept() {
        // @formatter:off
        givenJsonLdRequest(getClass(), "concept-scheme--create-with-top-concept.json")
                .when()
                    .post("/v0.1/concept-scheme")
                .then()
                    .statusCode(201)
                    .body("'dcterms:title'.'@value'", equalTo("Test"))
                    .body("'skos:hasTopConcept'.'dcterms:source'.'@id'", equalTo("urn:top-concept"));
        // @formatter:on
    }

}
