package com.digirati.taxman.common.rdf;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
     * Clear all RDF statements associated with a property, effectively emptying a list.
     *
     * @param property The property to clear.
     */
    default void clear(Property property) {
        getResource().removeAll(property);
    }

    /**
     * Get the Jena {@link Resource} that this model represents.
     *
     * @return The Jena {@code Resource} of this model.
     */
    default Resource getResource() {
        return getContext().getResource();
    }

    RdfModelContext getContext();

    default void add(Property property, RdfModel model) {
        getResource().addProperty(property, model.getResource());
    }

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
                        return factory.create(type, res, context.getAdditionalAttributes());
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
                        return factory.create(type, res, context.getAdditionalAttributes());
                    } catch (RdfModelException e) {
                        // Need a better way of re-throwing this as unchecked,
                        // ultimately, this only arises from programmer error.
                        throw new RuntimeException(e);
                    }
                });
    }

    default Map<String, String> getLiteral(Property property) {
        return Streams.stream(getResource().listProperties(property))
                .collect(Collectors.toMap(Statement::getLanguage, Statement::getString, (l, r) -> {
                    throw new RuntimeException("Attempted to fetch single-valued plain literal with multiple values");
                }));
    }
    /**
     * Get a map of {@code language} to {@code value} from a given RDF {@link Property} that represents
     * an {@code rdf:PlainLiteral}.
     *
     * @param property The plain literal lookup.
     * @return a map of localized literals.
     */
    default Multimap<String, String> getPlainLiteral(Property property) {
        Multimap<String, String> values = ArrayListMultimap.create();
        StmtIterator iterator = getResource().listProperties(property);

        while (iterator.hasNext()) {
            Statement stmt = iterator.nextStatement();
            RDFNode target = stmt.getObject();

            if (target instanceof Literal) {
                Literal literal = (Literal) target;
                values.put(literal.getLanguage(), literal.getLexicalForm());
            }
        }

        return values;
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
