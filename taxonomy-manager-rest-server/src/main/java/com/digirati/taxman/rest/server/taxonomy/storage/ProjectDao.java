package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ConceptSchemeRecordMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.record.mapper.ProjectRecordMapper;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

public class ProjectDao {

    private final ProjectRecordMapper projectRecordMapper = new ProjectRecordMapper();
    private final ConceptSchemeRecordMapper conceptSchemeRecordMapper = new ConceptSchemeRecordMapper();
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public ProjectDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public ProjectDataSet loadDataSet(String slug) {
        Object[] recordArgs = {slug};
        int[] recordTypes = {Types.OTHER};

        ProjectRecord project = jdbcTemplate.queryForObject("SELECT * FROM get_project(?)",
                recordArgs, recordTypes, projectRecordMapper);

        List<ConceptSchemeRecord> conceptSchemes = jdbcTemplate.query("SELECT * FROM get_project_concept_schemes(?)",
                recordArgs, recordTypes, conceptSchemeRecordMapper);

        return new ProjectDataSet(project, conceptSchemes);
    }

    public boolean storeDataSet(ProjectDataSet dataSet) {
        String slug = dataSet.getProject().getSlug();
        JSONObject title = new JSONObject(dataSet.getProject().getTitle());
        Object[] projectArgs = {slug, title};
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
}
