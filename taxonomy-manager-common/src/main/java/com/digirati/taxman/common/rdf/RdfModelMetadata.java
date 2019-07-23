package com.digirati.taxman.common.rdf;

import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * Metadata encompassing a type {@link T} and it's RDF related metadata ({@link Resource}
 * constructor and RDF type).
 */
public class RdfModelMetadata<T extends RdfModel> {

    /** The fully qualified RDF resource URI that the type represents. */
    public final Resource type;

    /**
     * The single argument constructor that creates instances of {@link T} from a {@link Resource}.
     */
    final Constructor<T> constructor;

    /**
     * A map of RDF namespace prefixes to URIs.
     *
     * <p>Example:
     * <code>
     *     Map.of("skos", "http://www.w3.org/2004/02/skos/core#")
     * </code>
     */
    final Map<String, String> namespacePrefixes;

    private RdfModelMetadata(
            Resource type, Constructor<T> constructor, Map<String, String> namespacePrefixes) {
        this.type = type;
        this.constructor = constructor;
        this.namespacePrefixes = namespacePrefixes;
    }

    /**
     * Resolve the {@link RdfModelMetadata} for a given {@link RdfModel} {@code type}.
     *
     * @param type The class of the type to resolve metadata for.
     * @param <T> The type to resolve metadata for.
     * @return the runtime metadata required to construct RDF models of the given {@code type}.
     * @throws RdfModelException if the class was missing the required metadata.
     */
    public static <T extends RdfModel> RdfModelMetadata<T> from(Class<T> type) throws RdfModelException {
        Constructor<T> constructor;

        try {
            constructor = type.getConstructor(RdfModelContext.class);
        } catch (NoSuchMethodException e) {
            throw new RdfModelException("No RdfModel constructor found", e);
        }

        var constructorDesignator = constructor.getAnnotation(RdfConstructor.class);
        if (constructorDesignator == null) {
            throw new RdfModelException(
                    "RdfModel constructor must be annotated with @RdfConstructor");
        }

        var ty = type.getAnnotation(RdfType.class);
        if (ty == null) {
            throw new RdfModelException("RdfModel class must be annotated with @RdfType");
        }

        var namespacesPrefixes = ImmutableMap.<String, String>builder();
        var context = type.getAnnotation(RdfContext.class);
        if (context != null) {
            for (String item : context.value()) {
                String[] parts = item.split("=");
                String prefix = parts[0];
                String uri = parts[1];

                namespacesPrefixes.put(prefix, uri);
            }
        }

        Model typeModel = ModelFactory.createDefaultModel();
        Resource typeResource = typeModel.createResource(ty.value());

        return new RdfModelMetadata<>(typeResource, constructor, namespacesPrefixes.build());
    }
}
