package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.PersistentProjectScopedModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelCreationListener;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@ApplicationScoped
public class RdfConfig {

    private static final Model m = ModelFactory.createDefaultModel();
    /**
     * The namespace of the SKOS vocabulary as a string
     */
    public static final String uri = "http://crugroup.com/commodities#";
    public static final Property inCommodityGroup = m.createProperty( uri + "inCommodityGroup");


    private static final Map<String, String> CONCEPT_TO_COMMODITY_GROUP = Map.ofEntries(
            Map.entry("Alumina", "Aluminium"),
            Map.entry("Aluminium Wire & Cable", "Aluminium"),
            Map.entry("Anodes", "Aluminium"),
            Map.entry("Bauxite", "Aluminium"),
            Map.entry("Beverage Can Sheet", "Aluminium"),
            Map.entry("Calcined Pet Coke", "Aluminium"),
            Map.entry("Carbon Products", "Aluminium"),
            Map.entry("Casthouse Shapes", "Aluminium"),
            Map.entry("Castings", "Aluminium"),
            Map.entry("Coal Tar Pitch", "Aluminium"),
            Map.entry("Extrusions", "Aluminium"),
            Map.entry("Primary Aluminium", "Aluminium"),
            Map.entry("Recycled Aluminium", "Aluminium"),
            Map.entry("Rolled Products", "Aluminium")
    );

    public static final String X_PROJECT_SLUG = "x-project-slug";

    public static void decorateProjectScopedModels(RdfModel model, Resource resource, Multimap<String, String> attributes) {
        if (!(model instanceof PersistentProjectScopedModel) || !attributes.containsKey(X_PROJECT_SLUG)) {
            return;
        }

        try {
            var projectIdValue = Iterables.getOnlyElement(attributes.get(X_PROJECT_SLUG));
            var projectScopedModel = (PersistentProjectScopedModel) model;

            projectScopedModel.setProjectId(projectIdValue);
        } catch (NoSuchElementException ex) {
            throw new WebApplicationException("X-Project-Slug must be present in the request headers");
        }
    }


    @Produces
    public RdfModelFactory rdfModelFactory() {
        return new RdfModelFactory(List.of(
                RdfConfig::decorateProjectScopedModels,
                RdfConfig::decorareCommodityGroupModels
        ));
    }

    private static void decorareCommodityGroupModels(RdfModel rdfModel, Resource resource, Multimap<String, String> stringStringMultimap) {
        if (!(rdfModel instanceof ConceptModel)) {
            return;
        }

        ConceptModel model = (ConceptModel) rdfModel;
        var commodityGroup = model.getPreferredLabel().values().stream().map(CONCEPT_TO_COMMODITY_GROUP::get).findFirst();
        commodityGroup.ifPresent(label -> {
            resource.addProperty(inCommodityGroup, label);
        });
    }
}
