package com.digirati.taxonomy.manager.storage.record.mapper;

import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.digirati.taxonomy.manager.storage.record.ConceptRelationshipRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ConceptRelationshipRecordMapper implements RowMapper<ConceptRelationshipRecord> {
    @Override
    public ConceptRelationshipRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID source = rs.getObject("source_uuid", UUID.class);
        UUID target = rs.getObject("target_uuid", UUID.class);
        ConceptRelationshipType type = ConceptRelationshipType.valueOf(rs.getString("type"));
        boolean transitive = rs.getBoolean("transitive");
        ConceptRelationshipRecord record = new ConceptRelationshipRecord(source, target, type, transitive);

        record.setPreferredLabel(ResultSetUtils.getPlainLiteralMap(rs, "target_preferred_label"));

        return record;
    }
}
