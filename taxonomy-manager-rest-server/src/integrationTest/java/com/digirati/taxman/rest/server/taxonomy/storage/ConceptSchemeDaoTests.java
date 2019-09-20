package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.testing.DatabaseTestExtension;
import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.MultimapBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
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
        var expectedTitle = ArrayListMultimap.<String, String>create();
        expectedTitle.put("en", "test");
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
        var expectedTopConcepts = List.of(new ConceptReference(conceptId, null, ArrayListMultimap.create()));

        dao.storeDataSet(new ConceptSchemeDataSet(record, expectedTopConcepts));

        var storedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);
        assertEquals(expectedTopConcepts, storedDataset.getTopConcepts());
    }

    @Test
    public void shouldRemoveTopConcepts() throws Exception {
        var conceptA = new ConceptReference(createDummyConcept(), null, ArrayListMultimap.create());
        var conceptB = new ConceptReference(createDummyConcept(), null, ArrayListMultimap.create());
        var conceptC = new ConceptReference(createDummyConcept(), null, ArrayListMultimap.create());

        var dao = new ConceptSchemeDao(dataSource);
        var record = new ConceptSchemeRecord(DUMMY_SCHEME_ID);

        dao.storeDataSet(new ConceptSchemeDataSet(record, List.of(conceptA, conceptB)));
        dao.storeDataSet(new ConceptSchemeDataSet(record, List.of(conceptB, conceptC)));

        var updatedDataset = dao.loadDataSet(DUMMY_SCHEME_ID);

        assertEquals(List.of(conceptB, conceptC), updatedDataset.getTopConcepts());
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
