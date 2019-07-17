package com.digirati.taxman.rest.taxonomy;

import javax.ws.rs.PathParam;

public class ProjectPath {

    @PathParam("project")
    private String projectSlug;

    public String getProjectSlug() {
        return projectSlug;
    }
}
