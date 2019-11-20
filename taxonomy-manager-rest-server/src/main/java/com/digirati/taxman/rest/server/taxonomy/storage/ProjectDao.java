package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptSchemeRecordMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ProjectRecordMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO for persisting and retrieving projects.
 */
public class ProjectDao {

    private final ProjectRecordMapper projectRecordMapper = new ProjectRecordMapper();
    private final ConceptSchemeRecordMapper conceptSchemeRecordMapper = new ConceptSchemeRecordMapper();
    private final DataSource dataSource;
    private final JdbcTemplateEx jdbcTemplate;

    public ProjectDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplateEx(dataSource);
    }

    /**
     * Retrieves a project with a given slug identifier.
     *
     * @param slug the identifier of the project to retrieve
     * @return the project with the given slug
     * @throws EmptyResultDataAccessException when no project can be found with the given slug
     */
    public Optional<ProjectDataSet> loadDataSet(String slug) throws EmptyResultDataAccessException {
        Object[] recordArgs = {slug};
        int[] recordTypes = {Types.VARCHAR};

        Optional<ProjectRecord> project = jdbcTemplate.queryForOptional("SELECT * FROM get_project(?)",
                recordArgs, recordTypes, projectRecordMapper);

        if (project.isEmpty()) {
            return Optional.empty();
        }

        List<ConceptSchemeRecord> conceptSchemes = jdbcTemplate.query("SELECT * FROM get_project_concept_schemes(?)",
                recordArgs, recordTypes, conceptSchemeRecordMapper);

        return Optional.of(new ProjectDataSet(project.get(), conceptSchemes));
    }

    public List<ProjectRecord> findAll() {
        return jdbcTemplate.query("SELECT * FROM get_all_projects()", new Object[]{}, new int[]{}, projectRecordMapper);
    }

    /**
     * Determines whether a project with a given slug already exists.
     *
     * @param slug the project slug to check
     * @return true if a project with the slug exists; false otherwise
     */
    public boolean projectExists(String slug) {
        return loadDataSet(slug).isPresent();
    }

    /**
     * Persists a project to the database. Note that this could be either a new project, or an update to an existing
     * project. In the case that this is called as an update, the association between the project and any schemes not
     * provided in the {@code dataSet} will be removed.
     *
     * @param dataSet the project to persist
     * @return true if any changes to existing records have occurred, false otherwise
     */
    public boolean storeDataSet(ProjectDataSet dataSet) {
        var project = dataSet.getProject();
        var slug = project.getSlug();
        var title = project.getTitle();

        Object[] projectArgs = {slug, DaoUtils.createRdfPlainLiteral(title)};
        int[] projectArgTypes = {Types.VARCHAR, Types.OTHER};

        int projectChanges = jdbcTemplate.update("CALL create_or_update_project(?, ?)", projectArgs, projectArgTypes);

        UUID[] schemeUuids = dataSet.getConceptSchemes().stream()
                .map(ConceptSchemeRecord::getUuid)
                .toArray(UUID[]::new);

        Array uuidArray = DaoUtils.createArrayOf(schemeUuids, "uuid", dataSource);
        Object[] schemeArgs = {slug, uuidArray};
        int[] schemeArgTypes = {Types.VARCHAR, Types.ARRAY};

        int schemeChanges = jdbcTemplate.update("CALL update_project_concept_schemes(?, ?)", schemeArgs, schemeArgTypes);

        return (projectChanges | schemeChanges) > 0;
    }

    public void deleteDataSet(String slug) {
        Object[] projectArgs = {slug};
        int[] projectArgTypes = {Types.VARCHAR};

        jdbcTemplate.update("CALL delete_project(?)", projectArgs, projectArgTypes);
    }
}
