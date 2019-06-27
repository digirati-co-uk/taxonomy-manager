package com.digirati.taxman.common.rdf.model;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.Map;

public class PlainLiteral {
    private final Resource resource;
    private final Property property;

    public PlainLiteral(Resource resource, Property property) {
        this.resource = resource;
        this.property = property;
    }

    public boolean hasValues() {
        return resource.listProperties(property).hasNext();
    }

    public Map<String, String> toMap() {
        return Map.of();
    }
}
