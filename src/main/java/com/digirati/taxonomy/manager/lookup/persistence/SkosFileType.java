package com.digirati.taxonomy.manager.lookup.persistence;

/**
 * Enum of all supported file types from which SKOS can be parsed, and to which the model of the
 * SKOS entities can be written.
 */
public enum SkosFileType {
    RDF_XML("RDF/XML"),
    N_TRIPLE("N-TRIPLE"),
    TURTLE("TTL"),
    N3("N3"),
    JSON_LD("JSON-LD"),
    TRIG("TRIG");

    private final String fileTypeName;

    SkosFileType(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }
}
