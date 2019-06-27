package com.digirati.taxman.rest;

import java.util.Map;
import javax.ws.rs.core.MediaType;

public final class MediaTypes {
    public static final String APPLICATION_JSONLD_SKOS_VALUE =
            "application/ld+json;profile=\"http://www.w3.org/2004/02/skos/core.jsonld\"";
    public static final String APPLICATION_RDF_XML_VALUE = "application/rdf+xml";

    public static final MediaType APPLICATION_RDF_XML = new MediaType("application", "rdf+xml");
    private static final Map<String, String> SKOS_PROFILE =
            Map.of("profile", "http://www.w3.org/2004/02/skos/core.jsonld");
    public static final MediaType APPLICATION_JSONLD_SKOS =
            new MediaType("application", "ld+json", SKOS_PROFILE);

    private MediaTypes() {}
}
