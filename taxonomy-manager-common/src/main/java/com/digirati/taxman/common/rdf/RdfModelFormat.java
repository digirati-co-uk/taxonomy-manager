package com.digirati.taxman.common.rdf;

import com.google.common.net.MediaType;
import org.apache.jena.riot.RDFFormat;

public enum RdfModelFormat {
    JSON_LD_FRAMED("JSON-LD", RDFFormat.JSONLD_FRAME_PRETTY, MediaType.create("application", "ld+json")),
    JSON_LD("JSON-LD", RDFFormat.JSONLD_COMPACT_PRETTY, MediaType.create("application", "rdf+xml")),
    RDFXML("RDFXML", RDFFormat.RDFXML_PRETTY, MediaType.create("application", "rdf+xml"));

    public static RdfModelFormat forMimeType(String mime) {
        MediaType type = MediaType.parse(mime);

        for (var format : values()) {
            if (format.media.is(type)) {
                return format;
            }
        }

        throw new IllegalArgumentException("Content with a type of '" + mime + "' has no matching RDF format");
    }

    private final String type;
    private final RDFFormat format;
    private final MediaType media;

    RdfModelFormat(String type, RDFFormat format, MediaType media) {
        this.type = type;
        this.format = format;
        this.media = media;
    }

    public String getType() {
        return type;
    }

    public RDFFormat getFormat() {
        return format;
    }
}
