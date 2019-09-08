package com.digirati.taxman.common.rdf;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A typed RDF model that represents a single composite node within a graph.
 */
public interface RdfModel {
    /**
     * Get the Jena {@link Resource} that this model represents.
     *
     * @return The Jena {@code Resource} of this model.
     */
    default Resource getResource() {
        return getContext().getResource();
    }

    RdfModelContext getContext();

    /**
     * Find all of the resources identified by the given {@code type} that appear in the same graph as this model,
     * regardless of any relations between them.
     *
     * @param type
     * @param <T>
     * @return
     */
    default <T extends RdfModel> Stream<T> getAllResources(Class<T> type) {
        var context = getContext();
        var model = getResource().getModel();
        var factory = context.getModelFactory();

        RdfModelMetadata metadata;
        try {
            metadata = RdfModelMetadata.from(type);
        } catch (RdfModelException e) {
            throw new RuntimeException(e);
        }

        var iter = model.listResourcesWithProperty(RDF.type, metadata.type);

        return Streams.stream(iter)
                .map(res -> {
                    try {
                        return factory.create(type, res);
                    } catch (RdfModelException e) {
                        // Need a better way of re-throwing this as unchecked,
                        // ultimately, this only arises from programmer error.
                        throw new RuntimeException(e);
                    }
                });
    }

    default <T extends RdfModel> Stream<T> getResources(Class<T> type, Property property) {
        var context = getContext();
        var resource = getResource();
        var factory = context.getModelFactory();

        return Streams.stream(resource.listProperties(property))
                .map(Statement::getResource)
                .map(res -> {
                    try {
                        return factory.create(type, res);
                    } catch (RdfModelException e) {
                        // Need a better way of re-throwing this as unchecked,
                        // ultimately, this only arises from programmer error.
                        throw new RuntimeException(e);
                    }
                });
    }

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
     * Gets a single String value from a given RDF {@link Property}.
     *
     * @param property the property to get
     * @return the String value of that property
     */
    default String getStringProperty(Property property) {
        return Iterables.getOnlyElement(Streams.stream(getResource().listProperties(property))
                .map(Statement::getString)
                .collect(Collectors.toSet()));
    }

    /**
     * Get the URI of the resource this {@code RdfModel} represents.
     *
     * @return the URI of this model.
     */
    default URI getUri() {
        return URI.create(getResource().getURI());
    }

    default Optional<RdfModel> buildEmbeddedModel(RdfModelFactory factory) {
        return Optional.empty();
    }
}
