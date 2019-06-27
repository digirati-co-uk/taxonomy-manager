package com.digirati.taxman.common.rdf;

import org.apache.jena.riot.RDFFormat;

public enum RdfModelFormat {
    JSON_LD_FRAMED("JSON-LD", RDFFormat.JSONLD_FRAME_PRETTY),
    JSON_LD("JSON-LD", RDFFormat.JSONLD_COMPACT_PRETTY),
    RDFXML("RDFXML", RDFFormat.RDFXML_PRETTY);

    private final String type;
    private final RDFFormat format;

    RdfModelFormat(String type, RDFFormat format) {
        this.type = type;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public RDFFormat getFormat() {
        return format;
    }
}
