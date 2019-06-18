package com.digirati.taxonomy.manager.storage;

import com.digirati.taxonomy.manager.storage.record.ConceptReference;
import com.digirati.taxonomy.manager.storage.record.ConceptSchemeRecord;
import com.digirati.taxonomy.manager.storage.record.mapper.ConceptReferenceMapper;
import com.digirati.taxonomy.manager.storage.record.mapper.ConceptSchemeRecordMapper;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

public class ConceptSchemeDao {

    private final JdbcTemplate jdbcTemplate;

    public ConceptSchemeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public ConceptSchemeRecord getConceptScheme(UUID uuid) {
        Object[] args = {uuid};
        int[] argTypes = {Types.OTHER};

        return jdbcTemplate.queryForObject(
                "SELECT * FROM get_concept_scheme(?)", args, argTypes, new ConceptSchemeRecordMapper());
    }

    public void createConceptScheme(ConceptSchemeRecord record) {
        Object[] args = {record.getUuid(), new JSONObject(record.getTitle())};
        int[] types = {Types.OTHER, Types.OTHER};

        jdbcTemplate.update("CALL create_concept_scheme(?, ?)", args, types);
    }

    public void updateConceptScheme(ConceptSchemeRecord record) {
        Object[] args = {record.getUuid(), new JSONObject(record.getTitle())};
        int[] types = {Types.OTHER, Types.OTHER};

        jdbcTemplate.update("CALL update_concept_scheme(?, ?)", args, types);
    }

    public List<ConceptReference> getTopConcepts(UUID schemeUuid) {
        Object[] args = {schemeUuid};
        int[] types = {Types.OTHER};

        return jdbcTemplate.query(
                "SELECT get_concept_scheme_top_concepts(?)",
                args,
                types,
                new ConceptReferenceMapper());
    }
}
