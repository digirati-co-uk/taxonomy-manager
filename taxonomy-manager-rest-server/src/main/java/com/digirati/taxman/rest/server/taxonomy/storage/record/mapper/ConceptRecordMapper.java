package com.digirati.taxman.rest.server.taxonomy.storage.record.mapper;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ConceptRecordMapper implements RowMapper<ConceptRecord> {
    @Override
    public ConceptRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConceptRecord record = new ConceptRecord(rs.getObject("uuid", UUID.class));

        record.setSource(rs.getString("source"));
        record.setProjectSlug(rs.getString("slug"));
        record.setSchemeUuid(rs.getObject("schemeUuid", UUID.class));
        record.setPreferredLabel(ResultSetUtils.getPlainLiteralMap(rs, "preferred_label"));
        record.setAltLabel(ResultSetUtils.getPlainLiteralMap(rs, "alt_label"));
        record.setHiddenLabel(ResultSetUtils.getPlainLiteralMap(rs, "hidden_label"));
        record.setNote(ResultSetUtils.getPlainLiteralMap(rs, "note"));
        record.setChangeNote(ResultSetUtils.getPlainLiteralMap(rs, "change_note"));
        record.setEditorialNote(ResultSetUtils.getPlainLiteralMap(rs, "editorial_note"));
        record.setExample(ResultSetUtils.getPlainLiteralMap(rs, "example"));
        record.setHistoryNote(ResultSetUtils.getPlainLiteralMap(rs, "history_note"));
        record.setScopeNote(ResultSetUtils.getPlainLiteralMap(rs, "scope_note"));

        return record;
    }
}
