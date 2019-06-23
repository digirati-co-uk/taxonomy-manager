package com.digirati.taxman.common.rdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class RdfModelFactory {
    public <T extends RdfModel> RdfModelBuilder<T> createBuilder(Class<T> type) throws RdfModelException {
        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(Map.copyOf(metadata.namespacePrefixes));

        return new RdfModelBuilder<>(this, model, metadata);
    }

    public <T extends RdfModel> T createEmptyModel(Class<T> type) throws RdfModelException {
        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefixes(Map.copyOf(metadata.namespacePrefixes));

        Resource resource = model.createResource(metadata.type);
        try {
            return metadata.constructor.newInstance(resource);
        } catch (ReflectiveOperationException e) {
            throw new RdfModelException("Unable to create RDF mapped model class", e);
        }
    }

    public <T extends RdfModel> T createFromResource(Class<T> type, Resource resource)
            throws RdfModelException {

        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);

        Model model = resource.getModel();
        model.setNsPrefixes(Map.copyOf(metadata.namespacePrefixes));

        try {
            return metadata.constructor.newInstance(resource);
        } catch (ReflectiveOperationException e) {
            throw new RdfModelException("Unable to create RDF mapped model class", e);
        }
    }

    public <T extends RdfModel> List<T> createListFromModel(Class<T> type, Model model)
            throws RdfModelException {

        var resources = new ArrayList<T>();

        RdfModelMetadata<T> metadata = RdfModelMetadata.from(type);
        model.setNsPrefixes(metadata.namespacePrefixes);

        ResIterator resourceIterator = model.listResourcesWithProperty(RDF.type, metadata.type);
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
}
