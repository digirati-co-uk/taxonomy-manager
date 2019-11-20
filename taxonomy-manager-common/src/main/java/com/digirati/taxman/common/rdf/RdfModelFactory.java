package com.digirati.taxman.common.rdf;

import com.digirati.taxman.common.rdf.identity.IdResolver;
import com.google.common.collect.Iterables;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A factory that produces {@link RdfModel}s and {@link RdfModelBuilder}s.
 */
public class RdfModelFactory {

    /**
     * Create a new {@link RdfModelBuilder} for models of the given {@code type}.
     *
     * @param type The class of the type of model to begin building.
     * @param <T>  The type of the model to begin building.
     * @return a new {@code RdfModelBuilder}.
     * @throws RdfModelException if the given {@code type} has no valid {@link RdfModelMetadata}>
     */
    public <T extends RdfModel> RdfModelBuilder<T> createBuilder(Class<T> type) throws RdfModelException {
        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(Map.copyOf(metadata.namespacePrefixes));

        return new RdfModelBuilder<>(this, model, metadata);
    }

    public <T extends RdfModel> T create(Class<T> type, Resource resource) throws RdfModelException {
        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);

        try {
            var uri = resource.getURI();

            // We don't want to set source, if the URI is our URI
            // Therefore, check for the UUID using our classes patterns
            var uuid = IdResolver.resolve(uri, metadata.pattern);
            var setSource = !uuid.isPresent();



            StmtIterator stmts = resource.listProperties();

            if (setSource && !resource.hasProperty(DCTerms.source) && uri != null && stmts.hasNext()) {
                resource.addProperty(DCTerms.source, resource);
            }

            return metadata.constructor.newInstance(new RdfModelContext(this, resource));
        } catch (ReflectiveOperationException e) {
            throw new RdfModelException("Unable to create RDF mapped model class", e);
        }
    }

    public <T extends RdfModel> T create(Class<T> type, Model model) throws RdfModelException {
        return Iterables.getOnlyElement(createListFromModel(type, model));
    }

    /**
     * Read a list of of typed models from the given RDF graph.
     *
     * @param type  The class of the type of model to begin building.
     * @param model The RDF graph to read data from.
     * @param <T>   The type of the model to begin building.
     * @return a {@link List} of typed models found within the RDF graph.
     * @throws RdfModelException if the given {@code type} has no valid {@link RdfModelMetadata}>
     */
    public <T extends RdfModel> List<T> createListFromModel(Class<T> type, Model model)
            throws RdfModelException {

        var resources = new ArrayList<T>();

        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);
        model.setNsPrefixes(metadata.namespacePrefixes);

        ResIterator resourceIterator = model.listResourcesWithProperty(RDF.type, metadata.type);
        while (resourceIterator.hasNext()) {
            Resource resource = resourceIterator.nextResource();
            resources.add(create(type, resource));
        }


        return resources;
    }
}
