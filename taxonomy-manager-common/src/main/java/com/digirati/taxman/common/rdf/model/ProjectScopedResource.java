package com.digirati.taxman.common.rdf.model;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import org.apache.jena.vocabulary.DCTerms;

public interface ProjectScopedResource extends RdfModel {
    /**
     * Get a reference to the {@link ProjectModel} this model belongs to.
     */
    default ProjectModel getProject() {
        return getResources(ProjectModel.class, DCTerms.isPartOf)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("A ProjectScopedResource(" + getClass().getName() + ") without a Project is valid"));
    }
}
