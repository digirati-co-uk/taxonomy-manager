package com.digirati.taxman.rest.server.infrastructure.media;

import org.apache.jena.ext.com.google.common.collect.ImmutableMap;

import javax.ws.rs.core.MediaType;
import java.util.Map;

/** A collection of {@link MediaType}s related to the SKOS JSON-LD data model. */
public class MediaTypes {
    private static final Map<String, String> SKOS_PROFILE =
            ImmutableMap.of("profile", "http://www.w3.org/2004/02/skos/core.jsonld");

    public static final MediaType APPLICATION_LD_JSON_WITH_SKOS =
            new MediaType("application", "ld+json", SKOS_PROFILE);

    public static final MediaType APPLICATION_RDF_XML = new MediaType("application", "rdf+xml");
}
