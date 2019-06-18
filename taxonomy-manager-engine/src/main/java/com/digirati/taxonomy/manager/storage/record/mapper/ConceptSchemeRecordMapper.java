package com.digirati.taxonomy.manager.storage.record.mapper;

import com.digirati.taxonomy.manager.storage.record.ConceptSchemeRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ConceptSchemeRecordMapper implements RowMapper<ConceptSchemeRecord> {

    @Override
    public ConceptSchemeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        ConceptSchemeRecord record = new ConceptSchemeRecord(rs.getObject("uuid", UUID.class));

        record.setTitle(ResultSetUtils.getPlainLiteralMap(rs, "title"));

        return record;
    }
}
