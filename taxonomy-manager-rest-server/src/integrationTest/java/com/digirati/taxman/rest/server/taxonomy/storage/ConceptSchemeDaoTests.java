package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.testing.DatabaseTestExtension;
import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(DatabaseTestExtension.class)
public class ConceptSchemeDaoTests {

    @TestDataSource DataSource dataSource;

    @Test
    public void shouldStoreLabels() {
        var dao = new ConceptSchemeDao(dataSource);

        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID);
        var expectedTitle = Map.of("en", "test");
        record.setTitle(expectedTitle);
        var dataset = new ConceptSchemeDataSet(record, List.of());

        dao.storeDataSet(dataset);

        var storedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);
        assertEquals(expectedTitle, storedDataset.getRecord().getTitle());
    }

    @Test
    public void shouldStoreTopConcepts() throws Exception {
        var conceptId = createDummyConcept();

        var dao = new ConceptSchemeDao(dataSource);
        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID);
        var expectedTopConcepts = List.of(new ConceptReference(conceptId, Map.of()));

        dao.storeDataSet(new ConceptSchemeDataSet(record, expectedTopConcepts));

        var storedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);
        assertEquals(expectedTopConcepts, storedDataset.getTopConcepts());
    }

    @Test
    public void shouldRemoveTopConcepts() throws Exception {
        var conceptId = createDummyConcept();

        var dao = new ConceptSchemeDao(dataSource);
        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID);
        var topConcepts = List.of(new ConceptReference(conceptId, Map.of()));

        dao.storeDataSet(new ConceptSchemeDataSet(record, topConcepts));
        dao.storeDataSet(new ConceptSchemeDataSet(record, List.of()));

        var updatedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);

        assertEquals(List.of(), updatedDataset.getTopConcepts());
    }


    private UUID createDummyConcept() throws SQLException {
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO skos_concept (uuid) VALUES (?)")) {

            stmt.setObject(1, DUMMY_CONCEPT_ID, Types.OTHER);
            assumeTrue(stmt.executeUpdate() > 0);
        }

        return DUMMY_CONCEPT_ID;
    }

    private static final UUID DUMMY_CONCEPT_ID = UUID.fromString("f0ea2717-1114-46f4-bc51-a25985571a01");
    private static final UUID DUMMY_SCHEME_ID = UUID.fromString("3828f4e5-ad0d-402c-978a-e2b9939332c7");
}
