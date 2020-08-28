package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.taxonomy.ConceptCollectionModelRepository;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxman.rest.taxonomy.ConceptPath;
import com.digirati.taxman.rest.taxonomy.ConceptRelationshipParams;
import com.digirati.taxman.rest.taxonomy.ConceptResource;
import com.digirati.taxman.rest.taxonomy.ConceptSchemePath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@ApplicationScoped
public class ServerConceptResource implements ConceptResource {

    @Inject
    ConceptCollectionModelRepository conceptCollections;

    @Inject
    ConceptModelRepository concepts;

    @Override
    public Response createConcept(@BeanParam ConceptSchemePath conceptSchemePath,  @Valid ConceptModel model) {
        var updatedModel =
                concepts.create(model, conceptSchemePath.getSchemeUuid(), conceptSchemePath.getProjectSlug());
        var uri = updatedModel.getUri();

        return Response.created(uri).entity(updatedModel).build();
    }

    @Override
    public Response getConcept(@BeanParam ConceptPath params) {
        var model = concepts.find(params.getConceptUuid());

        if (model.isPresent()) {
            return Response.ok(model.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response getRelationships(@BeanParam ConceptPath params,
                                     @Valid @BeanParam ConceptRelationshipParams relationshipParams) {

        var uuid = params.getConceptUuid();
        var type = relationshipParams.getType();
        var depth = relationshipParams.getDepth();

        return Response.ok(conceptCollections.findRelated(uuid, type, depth)).build();
    }

    @Override
    public Response getConceptsByPartialLabel(
            String partialLabel, String languageKey, UUID conceptSchemeUuid, String projectSlug) {
        CollectionModel matches =
                concepts.findByPartialLabel(partialLabel, languageKey, conceptSchemeUuid, projectSlug);

        return Response.ok(matches).build();
    }

    @Override
    public Response updateConcept(@BeanParam ConceptPath params, @Valid ConceptModel model) {
        model.setUuid(params.getConceptUuid());
        concepts.update(model, params.getSchemeUuid(), params.getProjectSlug());

        return Response.noContent().build();
    }

    @Override
    public Response deleteConcept(ConceptPath params) {
        concepts.delete(params.getConceptUuid(), params.getSchemeUuid(), params.getProjectSlug());

        return Response.noContent().build();
    }
}
