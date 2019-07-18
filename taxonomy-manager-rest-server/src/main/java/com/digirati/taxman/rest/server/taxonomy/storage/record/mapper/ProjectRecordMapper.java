package com.digirati.taxman.rest.server.taxonomy.storage.record.mapper;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectRecordMapper implements RowMapper<ProjectRecord> {

    @Override
    public ProjectRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectRecord projectRecord = new ProjectRecord(rs.getString("slug"));
        projectRecord.setTitle(ResultSetUtils.getPlainLiteralMap(rs, "title"));
        return projectRecord;
    }
}
