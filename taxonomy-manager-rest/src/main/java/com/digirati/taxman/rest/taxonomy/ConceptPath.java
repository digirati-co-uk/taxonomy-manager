package com.digirati.taxman.rest.taxonomy;

import javax.ws.rs.PathParam;

/** An addressable path to an element of a {@link ConceptResource}. */
public final class ConceptPath {

    private final String scheme;
    private final String concept;

    public ConceptPath(@PathParam("scheme") String scheme, @PathParam("concept") String concept) {
        this.scheme = scheme;
        this.concept = concept;
    }

    public String getScheme() {
        return scheme;
    }

    public String getConcept() {
        return concept;
    }
}
