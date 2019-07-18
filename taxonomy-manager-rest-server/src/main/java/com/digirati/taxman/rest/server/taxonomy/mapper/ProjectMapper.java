package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ProjectIdResolver;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDataSet;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;
import org.apache.jena.vocabulary.DCTerms;

import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps between RDF and database representations of a project.
 */
public class ProjectMapper {

    private final ProjectIdResolver projectIdResolver;

    private final ConceptSchemeIdResolver schemeIdResolver;

    private final RdfModelFactory modelFactory;

    public ProjectMapper(ProjectIdResolver projectIdResolver,
                         ConceptSchemeIdResolver schemeIdResolver,
                         RdfModelFactory modelFactory) {

        this.projectIdResolver = projectIdResolver;
        this.schemeIdResolver = schemeIdResolver;
        this.modelFactory = modelFactory;
    }

    public ProjectModel map(ProjectDataSet dataSet) {
        try {
            var builder = modelFactory.createBuilder(ProjectModel.class)
                    .setUri(projectIdResolver.resolve(dataSet.getProject().getSlug()))
                    .addStringProperty(DCTerms.identifier, dataSet.getProject().getSlug())
                    .addPlainLiteral(DCTerms.title, dataSet.getProject().getTitle());

            for (ConceptSchemeRecord conceptScheme : dataSet.getConceptSchemes()) {
                builder.addEmbeddedModel(DCTerms.hasPart,
                        modelFactory.createBuilder(ConceptSchemeModel.class)
                                .addPlainLiteral(DCTerms.title, conceptScheme.getTitle())
                                .setUri(schemeIdResolver.resolve(conceptScheme.getUuid())));
            }

            return builder.build();

        } catch (RdfModelException e) {
            throw new WebApplicationException("Internal error occurred creating RDF model from dataset", e);
        }
    }

    public ProjectDataSet map(String slug, ProjectModel model) {
        ProjectRecord record = new ProjectRecord(slug);
        record.setTitle(model.getTitle());

        List<ConceptSchemeRecord> schemes = model.getConceptSchemes()
                .map(scheme -> {
                    UUID uuid = schemeIdResolver.resolve(URI.create(scheme.getResource().getURI()));
                    ConceptSchemeRecord schemeRecord = new ConceptSchemeRecord(uuid);
                    schemeRecord.setTitle(scheme.getTitle());
                    return schemeRecord;
                })
                .collect(Collectors.toList());

        return new ProjectDataSet(record, schemes);
    }
}
