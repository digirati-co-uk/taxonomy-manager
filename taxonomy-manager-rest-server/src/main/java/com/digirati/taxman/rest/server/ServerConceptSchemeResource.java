package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.taxonomy.ConceptSchemeImporter;
import com.digirati.taxman.rest.server.taxonomy.ConceptSchemeModelRepository;
import com.digirati.taxman.rest.taxonomy.ConceptSchemePath;
import com.digirati.taxman.rest.taxonomy.ConceptSchemeResource;
import com.digirati.taxman.rest.taxonomy.ProjectPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.util.Optional;

@ApplicationScoped
public class ServerConceptSchemeResource implements ConceptSchemeResource {

    @Inject
    ConceptSchemeModelRepository conceptSchemes;

    @Inject
    ConceptSchemeImporter importer;

    @Override
    public Response createConceptScheme(ProjectPath projectPath, @Valid ConceptSchemeModel model) {
        var updatedModel = importer.importScheme(model, projectPath.getProjectSlug());
        var uri = updatedModel.getUri();

        return Response.created(uri).entity(updatedModel).build();
    }

    @Override
    public Response getConceptScheme(ConceptSchemePath params) {
        var model = conceptSchemes.find(params.getSchemeUuid());

        if (model.isPresent()) {
            return Response.ok(model.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response updateConceptScheme(ConceptSchemePath params, @Valid ConceptSchemeModel model) {
        model.setUuid(params.getSchemeUuid());
        conceptSchemes.update(model, params.getProjectSlug());

        return Response.noContent().build();
    }

    @Override
    public Response deleteConceptScheme(ConceptSchemePath params) {
        conceptSchemes.delete(params.getSchemeUuid(), params.getProjectSlug());

        return Response.noContent().build();
    }
}
