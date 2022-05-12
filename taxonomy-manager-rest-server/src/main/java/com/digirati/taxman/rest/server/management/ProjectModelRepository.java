package com.digirati.taxman.rest.server.management;

import com.digirati.taxman.common.rdf.RdfModelBuilder;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.infrastructure.exception.ProjectAlreadyExistsException;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxman.rest.server.taxonomy.ConceptSchemeImporter;
import com.digirati.taxman.rest.server.taxonomy.ConceptSchemeModelRepository;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectListingMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ProjectDataSet;
import org.apache.jena.vocabulary.DCTerms;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.digirati.taxman.rest.server.infrastructure.config.RdfConfig.X_PROJECT_SLUG;

@ApplicationScoped
public class ProjectModelRepository {

    @Inject
    ConceptSchemeImporter importer;

    @Inject
    ConceptSchemeModelRepository conceptSchemeModelRepository;

    @Inject
    ConceptModelRepository conceptRepository;

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
    @Transactional(Transactional.TxType.NEVER)
    public boolean update(String slug, ProjectModel project) {
        ProjectDataSet dataSet = projectMapper.map(slug, project);
        projectDao.storeDataSet(dataSet);

        // hack to propagate this context through RdfModelFactory
        project.getContext().getAdditionalAttributes().put(X_PROJECT_SLUG, slug);

        var uuids = new HashMap<String, UUID>();
        var conceptModels = project.getAllResources(ConceptModel.class).collect(Collectors.toUnmodifiableList());
        conceptModels.forEach(concept -> {
            if (concept.isNew()) {
                UUID uuid = UUID.randomUUID();

                uuids.put(concept.getSource(), uuid);
                concept.setUuid(uuid);
            }

            concept.setProjectId(slug);
            conceptRepository.update(concept);
        });

        var conceptSchemes = project.getAllResources(ConceptSchemeModel.class);
        conceptSchemes.forEach(scheme -> {
            if (scheme.isNew()) {
                scheme.setUuid(UUID.randomUUID());
            }

            List<ConceptModel> topConcepts = scheme.getTopConcepts().map(concept -> {
                concept.setUuid(uuids.get(concept.getSource()));
                return concept;
            })
            .collect(Collectors.toList());

            scheme.setTopConcepts(topConcepts);

            conceptSchemeModelRepository.update(scheme);
        });

        return true;
    }

    public void delete(String projectSlug) {
        projectDao.deleteDataSet(projectSlug);
    }
}
