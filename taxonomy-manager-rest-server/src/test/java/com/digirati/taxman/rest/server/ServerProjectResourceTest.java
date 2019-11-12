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

import static com.digirati.taxman.rest.server.testing.util.RestAssuredUtils.givenJsonLdRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
@Transactional
public class ServerProjectResourceTest {
    private static final Random RANDOM = new Random();
    @Test
    public void createProject() {
        // @formatter:off
        givenJsonLdRequest(getClass(), "project--create.json", Map.of("dcterms:identifier", "test-project-" + RANDOM.nextInt()))
                .when()
                .post("/v0.1/project")
                .then()
                .statusCode(200)
                .body("'dcterms:title'.'@value'", equalTo("Test Project"));
        // @formatter:on
    }

    @Test
    public void deleteProject_ReturnsNoContent() {
        // @formatter:off

        String identifier =
                givenJsonLdRequest(getClass(), "project--create.json", Map.of("dcterms:identifier", "test-project-" + RANDOM.nextInt()))
                        .when()
                        .post("/v0.1/project")
                        .then()
                        .extract()
                        .body()
                        .jsonPath()
                        .getString("'dcterms:identifier'");


        given()
                .when()
                .delete("/v0.1/project/" + identifier)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        // @formatter:on
    }

    @Test
    public void getProject_whenDeleted_notReturnsConcept() {
        // @formatter:off

        String identifier =
                givenJsonLdRequest(getClass(), "project--create.json", Map.of("dcterms:identifier", "test-project-" + RANDOM.nextInt()))
                        .when()
                        .post("/v0.1/project")
                        .then()
                        .extract()
                        .body()
                        .jsonPath()
                        .getString("'dcterms:identifier'");


        given()
                .when()
                .delete("/v0.1/project/" + identifier)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);


        given()
                .contentType("application/ld+json")
                .accept("application/ld+json")
                .when()
                .get("/v0.1/project/" + identifier)
                .then()
                .statusCode(Matchers.not(HttpStatus.SC_OK));

        // TODO: When proper responses are implemented, check for 410 gone or such

        // @formatter:on
    }
}
