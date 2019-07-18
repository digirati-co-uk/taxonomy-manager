package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;

import java.util.List;

public class ProjectDataSet {

    private final ProjectRecord project;

    private final List<ConceptSchemeRecord> conceptSchemes;

    public ProjectDataSet(ProjectRecord project, List<ConceptSchemeRecord> conceptSchemes) {
        this.project = project;
        this.conceptSchemes = conceptSchemes;
    }

    public ProjectRecord getProject() {
        return project;
    }

    public List<ConceptSchemeRecord> getConceptSchemes() {
        return conceptSchemes;
    }
}
