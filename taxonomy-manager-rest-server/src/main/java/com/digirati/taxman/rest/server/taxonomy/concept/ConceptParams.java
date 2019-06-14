package com.digirati.taxman.rest.server.taxonomy.concept;

import javax.ws.rs.PathParam;

/** An addressable path to an element of the {@link ConceptResource}. */
public class ConceptParams {
    @PathParam("scheme")
    String scheme;

    @PathParam("concept")
    String concept;
}
