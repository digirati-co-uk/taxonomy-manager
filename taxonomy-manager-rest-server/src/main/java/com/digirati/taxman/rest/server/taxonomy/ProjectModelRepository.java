package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.infrastructure.exception.ProjectAlreadyExistsException;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDataSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class ProjectModelRepository {

    @Inject
    ProjectDao projectDao;

    @Inject
    ProjectMapper projectMapper;

    /**
     * Stores a new project in the database, along with mappings between the project and any associated concept schemes.
     *
     * @param project the project to create
     * @return the created project
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ProjectModel create(ProjectModel project) throws ProjectAlreadyExistsException {
        if (projectDao.projectExists(project.getSlug())) {
            throw new ProjectAlreadyExistsException(project.getSlug());
        }
        ProjectDataSet dataSet = projectMapper.map(project);
        projectDao.storeDataSet(dataSet);
        return find(project.getSlug());
    }

    /**
     * Finds an RDF model representation of a project with a given slug.
     *
     * @param slug the slug of the project to search for
     * @return the project with the given slug
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public ProjectModel find(String slug) {
        ProjectDataSet dataSet = projectDao.loadDataSet(slug);
        return projectMapper.map(dataSet);
    }

    /**
     * Updates the details of an already-existing project in the database, including the set of concept schemes
     * associated with it.
     *
     * @param project the updated project
     * @return true if anything changed, false otherwise
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean update(ProjectModel project) {
        ProjectDataSet dataSet = projectMapper.map(project);
        return projectDao.storeDataSet(dataSet);
    }
}
