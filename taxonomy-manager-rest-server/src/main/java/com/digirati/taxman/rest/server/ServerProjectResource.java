package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.management.ProjectModelRepository;
import com.digirati.taxman.rest.taxonomy.ProjectPath;
import com.digirati.taxman.rest.taxonomy.ProjectResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

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
            return Response.ok(model.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response updateProject(ProjectPath projectPath, @Valid ProjectModel project) {
        projectModelRepository.update(projectPath.getProjectSlug(), project);
        return Response.noContent().build();
    }

    @Override
    public Response deleteProject(ProjectPath projectPath) {
        projectModelRepository.delete(projectPath.getProjectSlug());

        return Response.noContent().build();
    }
}
