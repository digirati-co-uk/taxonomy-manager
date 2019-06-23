package com.digirati.taxman.common.rdf;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.net.URI;
import java.util.Map;

import static com.digirati.taxman.common.rdf.RdfModelBuilder.PendingPropertyValue.Type.LITERAL;
import static com.digirati.taxman.common.rdf.RdfModelBuilder.PendingPropertyValue.Type.RESOURCE;

public class RdfModelBuilder<T extends RdfModel> {

    private final RdfModelFactory factory;
    private final Model model;
    private final RdfModelMetadata<T> metadata;
    private final Multimap<Property, PendingPropertyValue> properties = MultimapBuilder
            .hashKeys()
            .arrayListValues()
            .build();

    private URI uri;

    RdfModelBuilder(RdfModelFactory factory, Model model, RdfModelMetadata<T> metadata) {
        this.factory = factory;
        this.model = model;
        this.metadata = metadata;
    }

    public RdfModelBuilder addPlainLiteral(Property property, Map<String, String> values) {
        for (var entry : values.entrySet()) {
            String language = entry.getKey();
            String value = entry.getValue();
            Literal literal = model.createLiteral(value, language);

            properties.put(property, new PendingPropertyValue(literal));
        }

        return this;
    }

    public RdfModelBuilder addEmbeddedModel(Property property, RdfModelBuilder embeddedModel) throws RdfModelException {
        properties.put(property, new PendingPropertyValue(embeddedModel.build(model).getResource()));
        return this;
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
                    resource.addProperty(term, (Resource) value.value);
                    break;
            }
        }

        try {
            return metadata.constructor.newInstance(resource);
        } catch (ReflectiveOperationException e) {
            throw new RdfModelException("Unable to create RDF mapped model class", e);
        }
    }

    public T build() throws RdfModelException {
      return build(model);
    }

    public RdfModelBuilder<T> setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    static class PendingPropertyValue {
        final Type type;
        final Object value;

        PendingPropertyValue(Literal literal) {
            this.type = Type.LITERAL;
            this.value = literal;
        }

        PendingPropertyValue(Resource resource) {
            this.type = RESOURCE;
            this.value = resource;
        }

        enum Type {
            RESOURCE,
            LITERAL
        }
    }
}
