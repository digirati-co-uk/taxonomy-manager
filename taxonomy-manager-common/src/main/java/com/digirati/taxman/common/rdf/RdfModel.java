package com.digirati.taxman.common.rdf;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A typed RDF model that represents a single composite node within a graph.
 */
public interface RdfModel {
    /**
     * Get the Jena {@link Resource} that this model represents.
     *
     * @return The Jena {@code Resource} of this model.
     */
    Resource getResource();

    /**
     * Get a map of {@code language} to {@code value} from a given RDF {@link Property} that represents
     * an {@code rdf:PlainLiteral}.
     *
     * @param property The plain literal lookup.
     * @return a map of localized literals.
     */
    default Map<String, String> getPlainLiteral(Property property) {
        return Streams.stream(getResource().listProperties(property))
                .collect(
                        Collectors.toMap(
                                Statement::getLanguage, Statement::getString, (l, r) -> l));
    }

    /**
     * Get the URI of the resource this {@code RdfModel} represents.
     *
     * @return the URI of this model.
     */
    default URI getUri() {
        try {
            return new URI(getResource().getURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException("RdfModel has an invalid URI", ex);
        }
    }
}
