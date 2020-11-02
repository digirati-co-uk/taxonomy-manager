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
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RdfConfig {
    public static void decorateProjectScopedModels(RdfModel model, Resource resource, Multimap<String, String> attributes) {
        if (!(model instanceof PersistentProjectScopedModel) || !attributes.containsKey("X-Project-Id")) {
            return;
        }

        var projectIdValue = Iterables.getOnlyElement(attributes.get("X-Project-Id"));
        var projectId = UUID.fromString(projectIdValue);
        var projectScopedModel = (PersistentProjectScopedModel) model;

        projectScopedModel.setProjectId(projectId);
    }


    @Produces
    public RdfModelFactory rdfModelFactory() {
        return new RdfModelFactory(List.of(
                RdfConfig::decorateProjectScopedModels
        ));
    }
}
