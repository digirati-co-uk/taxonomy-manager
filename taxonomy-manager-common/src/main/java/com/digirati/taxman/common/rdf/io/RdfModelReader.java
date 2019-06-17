package com.digirati.taxman.common.rdf.io;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Iterables;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.JenaException;
import org.apache.jena.vocabulary.RDF;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic reader for annotated {@link RdfModel} classes, built as an easy way to provide
 * type-safe wrappers over RDF models.
 */
public class RdfModelReader {

    /**
     * Read a list of {@link RdfModel} types from a stream of input containing RDF data in any of
     * the {@link RdfModelFormat}s.
     *
     * @throws RdfModelException if an error occurred during reading of the RDF data.
     */
    public <T extends RdfModel> List<T> readAll(
            Class<T> type, RdfModelFormat format, InputStream modelStream)
            throws RdfModelException {

        var resources = new ArrayList<T>();

        Model model;
        try {
            model = ModelFactory.createDefaultModel();
            model.read(modelStream, null, format.getType());
        } catch (JenaException ex) {
            throw new RdfModelException("RDF error produced on deserialization", ex);
        }

        var metadata = RdfModelMetadata.from(type);

        ResIterator resourceIterator = model.listSubjectsWithProperty(RDF.type, metadata.type);
        while (resourceIterator.hasNext()) {
            Resource resource = resourceIterator.nextResource();

            try {
                resources.add(metadata.constructor.newInstance(resource));
            } catch (ReflectiveOperationException e) {
                throw new RdfModelException("Unable to create RDF mapped model class", e);
            }
        }

        return resources;
    }

    public <T extends RdfModel> T read(
            Class<T> type, RdfModelFormat format, InputStream modelStream)
            throws RdfModelException {
        return Iterables.getOnlyElement(readAll(type, format, modelStream));
    }

    /**
     * Metadata encompassing a type {@link T} and it's RDF related metadata ({@link Resource}
     * constructor and RDF type).
     */
    private static class RdfModelMetadata<T> {

        /** The fully qualified RDF resource URI that the type represents. */
        final Resource type;

        /**
         * The single argument constructor that creates instances of {@link T} from a {@link
         * Resource}.
         */
        final Constructor<T> constructor;

        RdfModelMetadata(Resource type, Constructor<T> constructor) {
            this.type = type;
            this.constructor = constructor;
        }

        static <T> RdfModelMetadata<T> from(Class<T> type) throws RdfModelException {
            Constructor<T> constructor;

            try {
                constructor = type.getConstructor(Resource.class);
            } catch (NoSuchMethodException e) {
                throw new RdfModelException("No RdfModel constructor found", e);
            }

            RdfConstructor constructorDesignator = constructor.getAnnotation(RdfConstructor.class);
            if (constructorDesignator == null) {
                throw new RdfModelException(
                        "RdfModel constructor must be annotated with @RdfConstructor");
            }

            RdfType ty = type.getAnnotation(RdfType.class);
            if (ty == null) {
                throw new RdfModelException("RdfModel class must be annotated with @RdfType");
            }

            Model typeModel = ModelFactory.createDefaultModel();
            Resource typeResource = typeModel.createResource(ty.value());

            return new RdfModelMetadata<>(typeResource, constructor);
        }
    }
}
