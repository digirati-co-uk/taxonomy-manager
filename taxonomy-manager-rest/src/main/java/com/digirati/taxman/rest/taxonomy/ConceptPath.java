package com.digirati.taxman.rest.taxonomy;

import javax.ws.rs.PathParam;
import java.util.UUID;

/** An addressable path to an element of a {@link ConceptResource}. */
public final class ConceptPath extends ConceptSchemePath {

    @PathParam("concept")
    private String concept;

    public ConceptPath() {}

    public ConceptPath(@PathParam("concept") String concept) {
        this.concept = concept;
    }

    public UUID getConceptUuid() {
        return UUID.fromString(concept);
    }
}
