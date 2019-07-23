package com.digirati.taxman.common.rdf;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.net.URI;
import java.util.Map;

import static com.digirati.taxman.common.rdf.RdfModelBuilder.PendingPropertyValue.Type.RESOURCE;

/**
 * A builder for typed {@link RdfModel}s.
 *
 * @param <T> The type of {@link RdfModel} being built.
 */
public class RdfModelBuilder<T extends RdfModel> {

    private final RdfModelFactory modelFactory;
    private final Model model;
    private final RdfModelMetadata<T> metadata;
    private final Multimap<Property, PendingPropertyValue> properties = MultimapBuilder
            .hashKeys()
            .arrayListValues()
            .build();

    private URI uri;

    RdfModelBuilder(RdfModelFactory modelFactory, Model model, RdfModelMetadata<T> metadata) {
        this.modelFactory = modelFactory;
        this.model = model;
        this.metadata = metadata;
    }

    /**
     * Add a new plain literal property to the underlying resource.
     *
     * @param property The property to add.
     * @param values   A map representing the plain literal, where keys are languages and
     *                 the values are the literal strings.
     */
    public RdfModelBuilder<T> addPlainLiteral(Property property, Map<String, String> values) {
        for (var entry : values.entrySet()) {
            String language = entry.getKey();
            String value = entry.getValue();
            Literal literal = model.createLiteral(value, language);

            properties.put(property, new PendingPropertyValue(literal));
        }

        return this;
    }

    /**
     * Add a new literal property to the underlying resource.
     *
     * @param property The property to literal is keyed on.
     * @param value The literal string value.
     */
    public RdfModelBuilder<T> addLiteral(Property property, String value) {
        properties.put(property, new PendingPropertyValue(model.createLiteral(value)));
        return this;
    }

    /**
     * Embed an existing {@link RdfModel} resource within the graph of this builder.
     *
     * @param property The property to associate the existing model with.
     * @param model    The model to add to the underlying RDF graph.
     */
    public RdfModelBuilder<T> addEmbeddedModel(Property property, RdfModel model) {
        properties.put(property, new PendingPropertyValue(model.getResource(), false));
        return this;
    }

    public RdfModelBuilder<T> addEmbeddedModel(Property property, URI uri) {
        properties.put(property, new PendingPropertyValue(model.getResource(uri.toASCIIString()), true));
        return this;
    }

    /**
     * Build and embed a new resource within this model and associate it with a {@code property}.
     *
     * @param property      The property to associate the resource with.
     * @param embeddedModel The builder used to produce the new embedded model.
     * @throws RdfModelException if the embedded resource was invalid.
     */
    public RdfModelBuilder<T> addEmbeddedModel(Property property,
                                               RdfModelBuilder embeddedModel) throws RdfModelException {
        var resource = embeddedModel.build(model).getResource();
        properties.put(property, new PendingPropertyValue(resource, true));

        return this;
    }

    /**
     * Adds a single String property to the underlying resource.
     *
     * @param property the type of property to add
     * @param value the value of that property
     * @return this {@link RdfModelBuilder}
     */
    public RdfModelBuilder<T> addStringProperty(Property property, String value) {
        properties.put(property, new PendingPropertyValue(model.createLiteral(value)));
        return this;
    }

    /**
     * Set the URI of the resource being built.
     *
     * @param uri The URI of the resource.
     */
    public RdfModelBuilder<T> setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Build and return the typed model.
     *
     * @return A new typed {@link RdfModel} with properties configured based on this builder.
     * @throws RdfModelException if the configured properties produced an invalid RDF graph.
     */
    public T build() throws RdfModelException {
        return build(model);
    }

    private T build(Model model) throws RdfModelException {
        Resource type = metadata.type;
        Resource resource = uri != null ? model.createResource(uri.toASCIIString(), type) : model.createResource(type);

        for (var pendingProperty : properties.entries()) {
            Property term = pendingProperty.getKey();
            PendingPropertyValue value = pendingProperty.getValue();

            switch (value.type) {
                case LITERAL:
                    resource.addLiteral(term, (Literal) value.value);
                    break;
                case RESOURCE:
                    var resourceValue = (Resource) value.value;
                    var resourceValueModel = resourceValue.getModel();
                    if (!value.embedded && !model.containsAll(resourceValueModel.listStatements())) {
                        model.add(resourceValueModel);
                    }

                    resource.addProperty(term, resourceValue);
                    break;
            }
        }

        try {
            return metadata.constructor.newInstance(new RdfModelContext(modelFactory, resource));
        } catch (ReflectiveOperationException e) {
            throw new RdfModelException("Unable to create RDF mapped model class", e);
        }
    }

    /**
     * A pending value that has no {@link Resource} to be attached to yet.
     *
     * <p>This is required because the URI of an existing {@code Resource} cannot be changed,
     * so we need to delegate creation of the object until all properties are configured and a
     * {@code URI} has been set (or not).
     */
    static class PendingPropertyValue {
        final Type type;
        final Object value;
        final boolean embedded;

        PendingPropertyValue(Literal literal) {
            this.type = Type.LITERAL;
            this.value = literal;
            this.embedded = true;
        }

        PendingPropertyValue(Resource resource, boolean embedded) {
            this.type = RESOURCE;
            this.value = resource;
            this.embedded = embedded;
        }

        enum Type {
            RESOURCE,
            LITERAL
        }
    }
}
