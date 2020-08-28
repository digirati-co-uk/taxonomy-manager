package com.digirati.taxman.rest.taxonomy;

import javax.ws.rs.PathParam;
import java.util.UUID;

public class ConceptSchemePath extends ProjectPath {

    @PathParam("scheme")
    private String scheme;

    public ConceptSchemePath() {
    }

    public ConceptSchemePath(@PathParam("scheme") String scheme) {
        this.scheme = scheme;
    }

    public UUID getSchemeUuid() {
        return UUID.fromString(scheme);
    }
}
