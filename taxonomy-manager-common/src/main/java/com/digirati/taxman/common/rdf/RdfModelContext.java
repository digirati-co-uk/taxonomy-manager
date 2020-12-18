package com.digirati.taxman.common.rdf;

import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class RdfModelContext {
    private final RdfModelFactory modelFactory;
    private final Resource resource;
    private final Multimap<String, String> additionalAttributes;
    private final Map<String, RdfModel> cache = new HashMap<>();

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

    public void cacheResource(String uri, RdfModel model) {
        cache.put(uri, model);
    }

    public <T extends RdfModel> Optional<T> getCachedResource(String uri) {
        return Optional.ofNullable((T)cache.get(uri));
    }

    public Multimap<String, String> getAdditionalAttributes() {
        return additionalAttributes;
    }
}
