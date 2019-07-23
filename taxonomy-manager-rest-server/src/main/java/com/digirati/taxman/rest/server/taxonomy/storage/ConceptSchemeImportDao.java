package com.digirati.taxman.rest.server.taxonomy.storage;

import javax.sql.DataSource;
import java.util.List;

public class ConceptSchemeImportDao {
    private final DataSource dataSource;

    public ConceptSchemeImportDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void importAll(ConceptSchemeDataSet conceptScheme, List<ConceptDataSet> dataSets) {

    }
}
