package com.digirati.taxman.rest.server.management;

import com.digirati.taxman.common.rdf.RdfModelBuilder;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.infrastructure.exception.ProjectAlreadyExistsException;
import com.digirati.taxman.rest.server.taxonomy.ConceptSchemeImporter;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectListingMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDataSet;
import org.apache.jena.vocabulary.DCTerms;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.digirati.taxman.rest.server.infrastructure.config.RdfConfig.X_PROJECT_SLUG;

@ApplicationScoped
public class ProjectModelRepository {

    @Inject
    ConceptSchemeImporter importer;

    @Inject
    ProjectDao projectDao;

    @Inject
    ProjectMapper projectMapper;

    @Inject
    ProjectListingMapper projectListingMapper;

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

        update(project.getSlug(), project);
        return find(project.getSlug()).orElseThrow();
    }

    /**
     * Finds an RDF model representation of a project with a given slug.
     *
     * @param slug the slug of the project to search for
     * @return the project with the given slug
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<ProjectModel> find(String slug) {
        var dataSet = projectDao.loadDataSet(slug);
        if (dataSet.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(projectMapper.map(dataSet.get()));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public CollectionModel listAll() {
        List<ProjectModel> projects = projectDao.findAll().stream()
                .map(record -> projectMapper.map(new ProjectDataSet(record, List.of())))
                .collect(Collectors.toList());
        return projectListingMapper.map(projects);
    }

    /**
     * Updates the details of an already-existing project in the database, including the set of concept schemes
     * associated with it.
     *
     * @param project the updated project
     * @return true if anything changed, false otherwise
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean update(String slug, ProjectModel project) {
        // hack to propagate this context through RdfModelFactory
        project.getContext().getAdditionalAttributes().put(X_PROJECT_SLUG, slug);

        List<ConceptSchemeModel> updatedSchemes = project.getAllResources(ConceptSchemeModel.class)
                .map(scheme -> importer.importScheme(scheme))
                .collect(Collectors.toList());

        // Get rid of the existing models.
        project.clear(DCTerms.hasPart);
        for (ConceptSchemeModel model : updatedSchemes) {
            project.add(DCTerms.hasPart, model);
        }

        projectDao.loadDataSet(slug);
        ProjectDataSet dataSet = projectMapper.map(slug, project);

        return projectDao.storeDataSet(dataSet);
    }

    public void delete(String projectSlug) {
        projectDao.deleteDataSet(projectSlug);
    }
}
