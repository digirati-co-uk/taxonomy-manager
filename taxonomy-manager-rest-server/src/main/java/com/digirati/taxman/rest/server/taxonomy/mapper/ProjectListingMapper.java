package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.taxonomy.identity.CollectionUriResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ProjectIdResolver;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import javax.ws.rs.WebApplicationException;
import java.util.Collection;

public class ProjectListingMapper {

    private final ProjectIdResolver projectIdResolver;

    private final CollectionUriResolver collectionUriResolver;

    private final RdfModelFactory factory;

    public ProjectListingMapper(ProjectIdResolver projectIdResolver,
                                CollectionUriResolver collectionUriResolver,
                                RdfModelFactory factory) {

        this.projectIdResolver = projectIdResolver;
        this.collectionUriResolver = collectionUriResolver;
        this.factory = factory;
    }

    public CollectionModel map(Collection<ProjectModel> projects) {
        try {
            var uri = collectionUriResolver.resolve();
            var builder = factory.createBuilder(CollectionModel.class)
                    .setUri(uri);

            for (ProjectModel project : projects) {
                builder.addEmbeddedModel(
                        SKOS.member,
                        factory.createBuilder(ConceptModel.class)
                                .addPlainLiteral(DCTerms.title, project.getTitle())
                                .setUri(projectIdResolver.resolve(project.getSlug())));
            }

            return builder.build();
        } catch (RdfModelException e) {
            throw new WebApplicationException("Internal error occurred creating Collection Model of projects", e);
        }
    }
}
