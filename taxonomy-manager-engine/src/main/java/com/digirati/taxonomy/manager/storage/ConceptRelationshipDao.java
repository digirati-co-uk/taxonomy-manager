package com.digirati.taxonomy.manager.storage;

import com.digirati.taxonomy.manager.storage.record.ConceptRelationshipRecord;
import com.digirati.taxonomy.manager.storage.record.mapper.ConceptRelationshipRecordMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

public final class ConceptRelationshipDao {
    private final JdbcTemplate jdbcTemplate;

    public ConceptRelationshipDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<ConceptRelationshipRecord> getRelationships(UUID sourceUuid) {
        Object[] args = {sourceUuid};
        int[] types = {Types.OTHER};

        return jdbcTemplate.query(
                "SELECT get_concept_relationships(?)",
                args,
                types,
                new ConceptRelationshipRecordMapper());
    }
}
