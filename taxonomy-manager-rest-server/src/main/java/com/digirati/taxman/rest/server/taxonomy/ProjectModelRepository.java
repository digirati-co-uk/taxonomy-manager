package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.ProjectModel;
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

    @Transactional(Transactional.TxType.REQUIRED)
    public ProjectModel create(ProjectModel project) {
        ProjectDataSet dataSet = projectMapper.map(project);
        projectDao.storeDataSet(dataSet);
        return find(project.getSlug());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public ProjectModel find(String slug) {
        ProjectDataSet dataSet = projectDao.loadDataSet(slug);
        return projectMapper.map(dataSet);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean store(ProjectModel project) {
        ProjectDataSet dataSet = projectMapper.map(project);
        return projectDao.storeDataSet(dataSet);
    }
}
