package com.digirati.taxonomy.manager.lookup.persistence;

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
