package com.digirati.taxman.rest.server.testing.util;

import io.restassured.specification.RequestSpecification;
import org.intellij.lang.annotations.Language;

import static io.restassured.RestAssured.given;

public final class RestAssuredUtils {
    private RestAssuredUtils() {}

    public static RequestSpecification givenJsonLdRequest(Class caller, @Language("file-reference") String body) {
        return given()
                .contentType("application/ld+json")
                .accept("application/ld+json")
                .body(caller.getResourceAsStream(body));
    }

}
