package com.digirati.taxman.rest.server.testing.util;

import io.restassured.specification.RequestSpecification;
import org.intellij.lang.annotations.Language;

import javax.json.Json;
import javax.json.JsonValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class RestAssuredUtils {
    private RestAssuredUtils() {}

    public static RequestSpecification givenJsonLdRequest(Class caller, @Language("file-reference") String body) {
        return givenJsonLdRequest(caller, body, Map.of());
    }

    public static RequestSpecification givenJsonLdRequest(Class caller, @Language("file-reference") String body, Map<String, String> mergeProperties) {
        String json;

        try (InputStream resourceAsStream = caller.getResourceAsStream(body)) {
            json = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to open request body identified by " + body);
        }

        var base = Json.createReader(new StringReader(json)).readObject();
        var merged = Json.createObjectBuilder(base);

        mergeProperties.forEach(merged::add);

        var output = new ByteArrayOutputStream();
        var writer = Json.createWriter(output);
        writer.write(merged.build());

        return given()
                .contentType("application/ld+json")
                .accept("application/ld+json")
                .body(output.toString(StandardCharsets.UTF_8));
    }

}
