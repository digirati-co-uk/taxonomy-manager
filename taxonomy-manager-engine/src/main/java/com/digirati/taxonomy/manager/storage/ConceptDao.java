package com.digirati.taxonomy.manager.storage;

import com.digirati.taxonomy.manager.storage.record.ConceptRecord;
import com.digirati.taxonomy.manager.storage.record.mapper.ConceptRecordMapper;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;
import java.util.UUID;

public class ConceptDao {
    private final JdbcTemplate jdbcTemplate;

    public ConceptDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createConcept(ConceptRecord record) {
        Object[] args = getCreateOrUpdateArguments(record);

        int[] types = new int[args.length];
        Arrays.fill(types, Types.OTHER);

        jdbcTemplate.update("CALL create_concept(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", args, types);
    }

    public ConceptRecord getConcept(UUID uuid) {
        Object[] args = {uuid};
        int[] types = {Types.OTHER};

        return jdbcTemplate.queryForObject(
                "SELECT * FROM get_concept(?)", args, types, new ConceptRecordMapper());
    }

    public void updateConcept(ConceptRecord record) {
        Object[] args = getCreateOrUpdateArguments(record);

        int[] types = new int[args.length];
        Arrays.fill(types, Types.OTHER);

        jdbcTemplate.update("CALL update_concept(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", args, types);
    }

    private static Object[] getCreateOrUpdateArguments(ConceptRecord record) {
        return new Object[] {
            record.getUuid(),
            new JSONObject(record.getPreferredLabel()),
            new JSONObject(record.getAltLabel()),
            new JSONObject(record.getHiddenLabel()),
            new JSONObject(record.getNote()),
            new JSONObject(record.getChangeNote()),
            new JSONObject(record.getEditorialNote()),
            new JSONObject(record.getExample()),
            new JSONObject(record.getHistoryNote()),
            new JSONObject(record.getScopeNote())
        };
    }
}
