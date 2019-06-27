package com.digirati.taxman.rest.taxonomy;

import javax.ws.rs.PathParam;
import java.util.UUID;

/** An addressable path to an element of a {@link ConceptResource}. */
public final class ConceptPath {

    @PathParam("concept")
    private String concept;

    public ConceptPath() {}

    public ConceptPath(@PathParam("concept") String concept) {
        this.concept = concept;
    }

    public UUID getUuid() {
        return UUID.fromString(concept);
    }
}
