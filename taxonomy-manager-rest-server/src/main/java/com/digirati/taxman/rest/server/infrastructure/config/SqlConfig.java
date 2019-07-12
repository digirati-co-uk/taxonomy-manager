package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptSchemeDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDao;
import io.agroal.api.AgroalDataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class SqlConfig {
    @Inject AgroalDataSource dataSource;

    @Produces
    public ConceptDao conceptDao() {
        return new ConceptDao(dataSource);
    }

    @Produces
    public ConceptSchemeDao conceptSchemeDao() {
        return new ConceptSchemeDao(dataSource);
    }

    @Produces
    public ProjectDao projectDao() {
        return new ProjectDao(dataSource);
    }
}
