package com.digirati.taxman.rest.server;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.transaction.Transactional;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.digirati.taxman.rest.server.testing.util.RestAssuredUtils.givenJsonLdRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class ServerConceptSchemeResourceTest {

    @Test
    @Transactional
    public void conceptScheme_Lifecycle() {
        // 1. Create new scheme
        String newSchemeLocation =
                givenJsonLdRequest(getClass(), "concept-scheme--create-without-source.json", Collections.emptyMap())
                        .when()
                        .post("/v0.1/concept-scheme")
                        .then()
                        .statusCode(201)
                        .extract()
                        .header("location");

        URI newSchemeUri = URI.create(newSchemeLocation);
        String[] pathSegments = newSchemeUri.getPath().split("/");
        String uuidStr = pathSegments[pathSegments.length - 1];
        //UUID uuid = UUID.fromString();

        // 2. Update it passing @id
        givenJsonLdRequest(getClass(), "concept-scheme--update-title-without-source.json", Map.of("@id", newSchemeLocation))
                .when()
                .put("/v0.1/concept-scheme/" + uuidStr)
                .then()
                .statusCode(204);

        // 3. Get and verify
        given()
                .when()
                .get("/v0.1/concept-scheme/" + uuidStr)
                .then()
                .statusCode(200)
                .body("'dcterms':'title'.'@language'", hasItems("en", "es"));

        // 4. Delete
        given()
                .when()
                .delete("/v0.1/concept-scheme/" + uuidStr)
                .then()
                .statusCode(204);
    }

    @Test
    @Transactional
    public void createConceptScheme_WithTopConcept() {
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
    @Transactional
    public void deleteConceptScheme_ReturnsNoContent() {
        // @formatter:off

        String conceptSchemeLocation =
                givenJsonLdRequest(getClass(), "concept-scheme--create-with-top-concept.json", Collections.emptyMap())
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
    @Transactional
    public void getConcept_whenDeleted_notReturnsConcept() {
        // @formatter:off

        String conceptSchemeLocation =
                givenJsonLdRequest(getClass(), "concept-scheme--create-with-top-concept.json", Collections.emptyMap())
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

        given()
                .contentType("application/ld+json")
                .accept("application/ld+json")
                .when()
                .get(URI.create(conceptSchemeLocation))
                .then()
                .statusCode(Matchers.not(HttpStatus.SC_OK));

        // TODO: When proper responses are implemented, check for 410 gone or such

        // @formatter:on
    }
}
