package com.digirati.taxman.common.rdf;

/**
 * An error type thrown during construction (or deconstruction) of RDF data into a object model.
 */
public class RdfModelException extends Exception {
    public RdfModelException(String message) {
        super(message);
    }

    public RdfModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
