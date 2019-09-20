package com.digirati.taxman.rest.server.taxonomy.storage.record.mapper;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.google.common.collect.Multimap;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class ConceptReferenceMapper implements RowMapper<ConceptReference> {
    @Override
    public ConceptReference mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID uuid = rs.getObject("uuid", UUID.class);
        var source = rs.getString("source");
        Multimap<String, String> preferredLabel = ResultSetUtils.getPlainLiteralMap(rs, "preferred_label");

        return new ConceptReference(uuid, source, preferredLabel);
    }
}
