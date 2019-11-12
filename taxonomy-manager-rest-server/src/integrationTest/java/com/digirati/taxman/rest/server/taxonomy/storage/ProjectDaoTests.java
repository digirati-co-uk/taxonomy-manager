package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;
import com.digirati.taxman.rest.server.testing.DatabaseTestExtension;
import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import com.google.common.collect.ArrayListMultimap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@ExtendWith(DatabaseTestExtension.class)
class ProjectDaoTests {

    @TestDataSource
    private DataSource dataSource;

    @Test
    void shouldStoreTitle() {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ProjectRecord record = new ProjectRecord("test-project");
        var title = ArrayListMultimap.<String, String>create();
        title.put("en", "Test Project");
        record.setTitle(title);

        // When
        dao.storeDataSet(new ProjectDataSet(record, new ArrayList<>()));
        Optional<ProjectDataSet> retrieved = dao.loadDataSet("test-project");

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(title, retrieved.get().getProject().getTitle());
    }

    @Test
    void shouldStoreNewSchemes() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord schemeA = new ConceptSchemeRecord(createDummyScheme());
        ConceptSchemeRecord schemeB = new ConceptSchemeRecord(createDummyScheme());
        List<ConceptSchemeRecord> schemes = List.of(schemeA, schemeB);

        var title = ArrayListMultimap.<String, String>create();
        title.put("en", "Test");

        ProjectRecord record = new ProjectRecord("test-project");
        record.setTitle(title);

        ProjectDataSet project = new ProjectDataSet(record, schemes);

        // When
        dao.storeDataSet(project);
        Optional<ProjectDataSet> retrieved = dao.loadDataSet("test-project");

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(schemes, retrieved.get().getConceptSchemes());
    }

    @Test
    void shouldRemoveSchemesWhichAreNoLongerAssociatedWithProject() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord schemeA = new ConceptSchemeRecord(createDummyScheme());
        ConceptSchemeRecord schemeB = new ConceptSchemeRecord(createDummyScheme());
        ConceptSchemeRecord schemeC = new ConceptSchemeRecord(createDummyScheme());

        var title = ArrayListMultimap.<String, String>create();
        title.put("en", "Test");

        ProjectRecord record = new ProjectRecord("test-project");
        record.setTitle(title);

        ProjectDataSet originalProject = new ProjectDataSet(record, List.of(schemeA, schemeB));
        dao.storeDataSet(originalProject);

        List<ConceptSchemeRecord> updatedSchemeList = List.of(schemeB, schemeC);
        ProjectDataSet updateProject = new ProjectDataSet(record, updatedSchemeList);

        // When
        dao.storeDataSet(updateProject);
        Optional<ProjectDataSet> retrieved = dao.loadDataSet("test-project");

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(updatedSchemeList, retrieved.get().getConceptSchemes());
    }

    @Test
    void shouldListAllProjects() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord scheme = new ConceptSchemeRecord(createDummyScheme());

        ProjectRecord record1 = new ProjectRecord("project-1");
        var title1 = ArrayListMultimap.<String, String>create();
        title1.put("en", "Project 1");
        record1.setTitle(title1);

        ProjectDataSet dataSet1 = new ProjectDataSet(record1, List.of(scheme));
        dao.storeDataSet(dataSet1);

        ProjectRecord record2 = new ProjectRecord("project-2");
        var title2 = ArrayListMultimap.<String, String>create();
        title2.put("en", "Project 2");
        record2.setTitle(title2);

        ProjectDataSet dataSet2 = new ProjectDataSet(record2, List.of(scheme));
        dao.storeDataSet(dataSet2);

        // When
        List<ProjectRecord> allProjects = dao.findAll();

        // Then
        assertEquals(List.of(record1, record2), allProjects);
    }

    private UUID createDummyScheme() throws SQLException {
        UUID schemeUuid = UUID.randomUUID();
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement("INSERT INTO skos_concept_scheme (uuid, title) VALUES (?, '{}')")) {
            stmt.setObject(1, schemeUuid, Types.OTHER);
            assumeTrue(stmt.executeUpdate() > 0);
        }
        return schemeUuid;
    }
}
