package com.digirati.taxman.common.rdf;

public enum RdfModelFormat {
    JSON_LD("JSON-LD"),
    RDFXML("RDFXML");

    private final String type;

    RdfModelFormat(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
