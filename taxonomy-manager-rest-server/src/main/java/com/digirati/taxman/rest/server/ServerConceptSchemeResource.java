package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.ConceptSchemeImportModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxman.rest.server.taxonomy.ConceptSchemeModelRepository;
import com.digirati.taxman.rest.taxonomy.ConceptSchemePath;
import com.digirati.taxman.rest.taxonomy.ConceptSchemeResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ServerConceptSchemeResource implements ConceptSchemeResource {

    @Inject
    ConceptSchemeModelRepository conceptSchemes;

    @Inject
    ConceptModelRepository concepts;

    @Override
    public Response createConceptScheme(@Valid ConceptSchemeModel model) {
        var updatedModel = conceptSchemes.create(model);
        var uri = updatedModel.getUri();

        return Response.created(uri).entity(updatedModel).build();
    }

    @Override
    public Response importConceptScheme(ConceptSchemeImportModel conceptSchemeImport) {
        var updatedModel = conceptSchemes.importScheme(conceptSchemeImport);
        var uri = updatedModel.getUri();

        return Response.created(uri).entity(updatedModel).build();
    }

    @Override
    public Response getConceptScheme(ConceptSchemePath params) {
        var model = conceptSchemes.find(params.getUuid());

        return Response.ok(model).build();
    }

    @Override
    public Response updateConceptScheme(ConceptSchemePath params, @Valid ConceptSchemeModel model) {
        model.setUuid(params.getUuid());
        conceptSchemes.update(model);

        return Response.noContent().build();
    }
}
