package com.digirati.taxonomy.manager.storage;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConceptSchemeDao {

    private final JdbcTemplate jdbcTemplate;

    public ConceptSchemeDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
