package com.digirati.taxman.common.rdf;

import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Resource;

import java.util.UUID;

public final class RdfModelContext {
    private final RdfModelFactory modelFactory;
    private final Resource resource;
    private final Multimap<String, String> additionalAttributes;

    public RdfModelContext(RdfModelFactory modelFactory, Resource resource, Multimap<String, String> additionalAttributes) {
        this.modelFactory = modelFactory;
        this.resource = resource;
        this.additionalAttributes = additionalAttributes;
    }

    /**
     * Get the {@link RdfModelFactory} responsible for creating models in this context.
     */
    public RdfModelFactory getModelFactory() {
        return modelFactory;
    }

    public Resource getResource() {
        return resource;
    }

    public Multimap<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }
}
