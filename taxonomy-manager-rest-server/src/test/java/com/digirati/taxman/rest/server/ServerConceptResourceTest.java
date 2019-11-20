package com.digirati.taxman.rest.server;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static com.digirati.taxman.rest.server.testing.util.RestAssuredUtils.givenJsonLdRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class ServerConceptResourceTest {

    @Test
    public void createConcept_JsonLd() {
        // @formatter:off
        givenJsonLdRequest(getClass(), "concept--create.json", Collections.emptyMap())
                .when()
                    .post("/v0.1/concept")
                .then()
                    .statusCode(201);
        // @formatter:on
    }

    @Test
    public void createConcept_JsonLdFailsValidationNoPrefLabel() {
        // @formatter:off
        givenJsonLdRequest(getClass(), "concept--create-no-pref-label.json", Collections.emptyMap())
                .when()
                    .post("/v0.1/concept")
                .then()
                    .statusCode(422);
        // @formatter:on
    }

    @Test
    public void createUpdate_RetainsDctermsSource() {
        // @formatter:off
        String conceptLocation =
                givenJsonLdRequest(getClass(), "concept--create-with-uri.json")
                    .when()
                        .post("/v0.1/concept")
                    .then()
                        .body("'dcterms:source'.'@id'", equalTo("https://example.org/my-id-001"))
                    .and()
                    .extract()
                        .header("Location");

        givenJsonLdRequest(getClass(), "concept--update-with-source.json", Map.of("@id", conceptLocation))
                .when()
                    .put(conceptLocation)
                .then()
                    .statusCode(204);

        given()
                .contentType("application/ld+json")
                .accept("application/ld+json")
                .when()
                    .get(URI.create(conceptLocation))
                .then()
                    .body("'dcterms:source'.'@id'", equalTo("https://example.org/my-id-001"));

        // @formatter:on
    }

    @Test
    public void getConcept_whenDeleted_notReturnsConcept() {
        String conceptLocation =
                givenJsonLdRequest(getClass(), "concept--delete-with-uri.json")
                        .when()
                            .post("/v0.1/concept")
                        .then()
                            .extract()
                            .header("Location");

        given()
                .when()
                    .delete(URI.create(conceptLocation))
                .then()
                    .statusCode(HttpStatus.SC_NO_CONTENT);

        given()
                .contentType("application/ld+json")
                .accept("application/ld+json")
                .when()
                    .get(URI.create(conceptLocation))
                .then()
                    .statusCode(Matchers.not(HttpStatus.SC_OK));

        // TODO: When proper responses are implemented, check for 410 gone or such

        // @formatter:on
    }


}
