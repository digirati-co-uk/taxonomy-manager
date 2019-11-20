package com.digirati.taxman.rest.server;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import java.net.URI;
import java.util.Collections;

import static com.digirati.taxman.rest.server.testing.util.RestAssuredUtils.givenJsonLdRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class ServerConceptSchemeResourceTest {

    @Test
    public void createConceptScheme_WithTopConcept() throws Exception {
        // @formatter:off
        givenJsonLdRequest(getClass(), "concept-scheme--create-with-top-concept.json", Collections.emptyMap())
                .when()
                .post("/v0.1/concept-scheme")
                .then()
                .statusCode(201)
                .body("'dcterms:title'.'@value'", equalTo("Test"))
                .body("'skos:hasTopConcept'.'dcterms:source'.'@id'", equalTo("urn:top-concept"));
        // @formatter:on
    }

    @Test
    public void deleteConceptScheme_ReturnsNoContent() throws Exception {
        String conceptSchemeLocation =
                givenJsonLdRequest(getClass(), "concept-scheme--delete.json", Collections.emptyMap())
                        .when()
                        .post("/v0.1/concept-scheme")
                        .then()
                        .extract()
                        .header("Location");

        given()
                .when()
                .delete(URI.create(conceptSchemeLocation))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        // @formatter:on
    }

    @Test
    public void updateConceptScheme_reject_empty_title() throws Exception {
        // @formatter:off
        String location = givenJsonLdRequest(getClass(), "concept-scheme--test-empty-title.json", Collections.emptyMap())
                .when()
                .post("/v0.1/concept-scheme")
                .then()
                .statusCode(201)
                .and()
                .extract()
                .header("Location");


        givenJsonLdRequest(getClass(), "concept-scheme--test-empty-title-2.json", Collections.emptyMap())
                .when()
                .put(location)
                .then()
                .statusCode(422);

        // @formatter:on
    }

}
