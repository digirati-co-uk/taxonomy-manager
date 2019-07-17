package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.taxonomy.ProjectModelRepository;
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
        return Response.ok(projectModelRepository.find(projectPath.getProjectSlug())).build();
    }

    @Override
    public Response updateProject(ProjectPath projectPath, @Valid ProjectModel project) {
        projectModelRepository.update(projectPath.getProjectSlug(), project);
        return Response.noContent().build();
    }
}
