package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.infrastructure.config.RdfConfig;
import com.digirati.taxman.rest.server.management.ProjectModelRepository;
import com.digirati.taxman.rest.server.taxonomy.ExtraTripleBank;
import com.digirati.taxman.rest.taxonomy.ProjectPath;
import com.digirati.taxman.rest.taxonomy.ProjectResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.net.URI;

@ApplicationScoped
public class ServerProjectResource implements ProjectResource {

    @Inject
    ProjectModelRepository projectModelRepository;

    @Override
    public Response listProjects() {
        return Response.ok(projectModelRepository.listAll()).build();
    }

    @Override
    public Response createProject(@Valid ProjectModel project) {
        return Response.ok(projectModelRepository.create(project)).build();
    }

    @Override
    public Response getProject(ProjectPath projectPath) {
        var model = projectModelRepository.find(projectPath.getProjectSlug());
        if (model.isPresent()) {
            var resource = model.get().getResource();
            ExtraTripleBank.getStatementsFor(resource)
                    .forEach(stmt -> {
                        resource.addProperty(stmt.getPredicate().inModel(resource.getModel()), stmt.getObject().inModel(resource.getModel()));
                    });
            return Response.ok(model.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response updateProject(ProjectPath projectPath, @Valid ProjectModel project) {
        ExtraTripleBank.storeStatementsFrom(project.getResource());

        projectModelRepository.update(projectPath.getProjectSlug(), project);
        return Response.noContent().build();
    }

    @Override
    public Response deleteProject(ProjectPath projectPath) {
        projectModelRepository.delete(projectPath.getProjectSlug());

        return Response.noContent().build();
    }
}
