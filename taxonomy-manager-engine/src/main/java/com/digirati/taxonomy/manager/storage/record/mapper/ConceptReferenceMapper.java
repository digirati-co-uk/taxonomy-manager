package com.digirati.taxonomy.manager.storage.record.mapper;

import com.digirati.taxonomy.manager.storage.record.ConceptReference;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class ConceptReferenceMapper implements RowMapper<ConceptReference> {
    @Override
    public ConceptReference mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID uuid = rs.getObject("uuid", UUID.class);
        Map<String, String> preferredLabel = ResultSetUtils.getPlainLiteralMap(rs, "preferred_label");

        return new ConceptReference(uuid, preferredLabel);
    }
}
