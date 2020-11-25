package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.testing.DatabaseTestExtension;
import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import com.google.common.collect.ArrayListMultimap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(DatabaseTestExtension.class)
public class ConceptSchemeDaoTests {

    @TestDataSource DataSource dataSource;

    @Test
    public void shouldStoreLabels() {
        var dao = new ConceptSchemeDao(dataSource);

        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID, "project-slug");
        var expectedTitle = ArrayListMultimap.<String, String>create();
        expectedTitle.put("en", "test");
        record.setTitle(expectedTitle);

        var dataset = new ConceptSchemeDataSet(record, List.of());

        dao.storeDataSet(dataset);

        var storedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);
        assertTrue(storedDataset.isPresent());
        assertEquals(expectedTitle, storedDataset.get().getRecord().getTitle());
    }

    @Test
    public void shouldStoreTopConcepts() throws Exception {
        var conceptId = createDummyConcept();

        var dao = new ConceptSchemeDao(dataSource);
        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID, "project-slug");
        var expectedTopConcepts = List.of(new ConceptRecord(conceptId, "project-slug"));

        dao.storeDataSet(new ConceptSchemeDataSet(record, expectedTopConcepts));

        var storedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);
        assertTrue(storedDataset.isPresent());
        assertEquals(expectedTopConcepts, storedDataset.get().getTopConcepts());
    }

    @Test
    public void shouldRemoveTopConcepts() throws Exception {
        var conceptA = new ConceptRecord(createDummyConcept(), "project-slug");
        var conceptB = new ConceptRecord(createDummyConcept(), "project-slug");
        var conceptC = new ConceptRecord(createDummyConcept(), "project-slug");

        var dao = new ConceptSchemeDao(dataSource);
        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID, "project-slug");

        dao.storeDataSet(new ConceptSchemeDataSet(record, List.of(conceptA, conceptB)));
        dao.storeDataSet(new ConceptSchemeDataSet(record, List.of(conceptB, conceptC)));

        var updatedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);

        assertTrue(updatedDataset.isPresent());
        assertEquals(List.of(conceptB, conceptC), updatedDataset.get().getTopConcepts());
    }

    private UUID createDummyConcept() throws SQLException {
        UUID uuid = UUID.randomUUID();
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO skos_concept (uuid) VALUES (?)")) {

            stmt.setObject(1, uuid, Types.OTHER);
            assumeTrue(stmt.executeUpdate() > 0);
        }

        return uuid;
    }

    private static final UUID DUMMY_SCHEME_ID = UUID.fromString("3828f4e5-ad0d-402c-978a-e2b9939332c7");
}
