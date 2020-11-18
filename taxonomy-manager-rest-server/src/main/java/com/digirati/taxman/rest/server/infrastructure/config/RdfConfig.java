package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.PersistentProjectScopedModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelCreationListener;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@ApplicationScoped
public class RdfConfig {
    public static void decorateProjectScopedModels(RdfModel model, Resource resource, Multimap<String, String> attributes) {
        if (!(model instanceof PersistentProjectScopedModel) || !attributes.containsKey("X-Project-Slug")) {
            return;
        }

        try {
            var projectIdValue = Iterables.getOnlyElement(attributes.get("X-Project-Slug"));
            var projectScopedModel = (PersistentProjectScopedModel) model;

            projectScopedModel.setProjectId(projectIdValue);
        } catch (NoSuchElementException ex) {
            throw new WebApplicationException("X-Project-Slug must be present in the request headers");
        }
    }


    @Produces
    public RdfModelFactory rdfModelFactory() {
        return new RdfModelFactory(List.of(
                RdfConfig::decorateProjectScopedModels
        ));
    }
}
