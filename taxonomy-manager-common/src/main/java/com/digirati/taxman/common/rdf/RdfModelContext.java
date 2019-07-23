package com.digirati.taxman.common.rdf;

import org.apache.jena.rdf.model.Resource;

import java.util.UUID;

public final class RdfModelContext {
    private final RdfModelFactory modelFactory;
    private final Resource resource;

    public RdfModelContext(RdfModelFactory modelFactory, Resource resource) {
        this.modelFactory = modelFactory;
        this.resource = resource;
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
}
