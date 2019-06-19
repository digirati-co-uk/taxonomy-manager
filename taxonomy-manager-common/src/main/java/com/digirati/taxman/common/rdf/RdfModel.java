package com.digirati.taxman.common.rdf;

import com.google.common.collect.Streams;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public interface RdfModel {
    Resource getResource();

    default Map<String, String> getPlainLiteral(Property property) {
        return Streams.stream(getResource().listProperties(property))
            .collect(
                Collectors.toMap(
                    Statement::getLanguage, Statement::getString, (l, r) -> l));
    }

    default URI getUri() {
        try {
            return new URI(getResource().getURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException("RdfModel has an invalid URI", ex);
        }
    }
}
