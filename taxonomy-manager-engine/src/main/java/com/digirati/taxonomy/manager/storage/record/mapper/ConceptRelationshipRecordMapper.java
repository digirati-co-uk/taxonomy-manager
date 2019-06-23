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
        var source = rs.getObject("source_uuid", UUID.class);
        var target = rs.getObject("target_uuid", UUID.class);
        var type = ConceptRelationshipType.valueOf(rs.getString("relation").toUpperCase());
        var transitive = rs.getBoolean("transitive");
        var record = new ConceptRelationshipRecord(source, target, type, transitive);

        record.setPreferredLabel(ResultSetUtils.getPlainLiteralMap(rs, "target_preferred_label"));

        return record;
    }
}
