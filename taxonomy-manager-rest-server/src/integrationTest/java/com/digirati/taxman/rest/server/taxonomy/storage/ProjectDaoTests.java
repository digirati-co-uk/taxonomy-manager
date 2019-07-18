package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ProjectRecord;
import com.digirati.taxman.rest.server.testing.DatabaseTestExtension;
import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        Map<String, String> title = Map.of("en", "Test Project");
        record.setTitle(title);

        // When
        dao.storeDataSet(new ProjectDataSet(record, new ArrayList<>()));
        ProjectDataSet retrieved = dao.loadDataSet("test-project");

        // Then
        assertEquals(title, retrieved.getProject().getTitle());
    }

    @Test
    void shouldStoreNewSchemes() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord schemeA = new ConceptSchemeRecord(createDummyScheme());
        ConceptSchemeRecord schemeB = new ConceptSchemeRecord(createDummyScheme());
        List<ConceptSchemeRecord> schemes = List.of(schemeA, schemeB);
        ProjectRecord record = new ProjectRecord("test-project");
        ProjectDataSet project = new ProjectDataSet(record, schemes);

        // When
        dao.storeDataSet(project);
        ProjectDataSet retrieved = dao.loadDataSet("test-project");

        // Then
        assertEquals(schemes, retrieved.getConceptSchemes());
    }

    @Test
    void shouldRemoveSchemesWhichAreNoLongerAssociatedWithProject() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord schemeA = new ConceptSchemeRecord(createDummyScheme());
        ConceptSchemeRecord schemeB = new ConceptSchemeRecord(createDummyScheme());
        ConceptSchemeRecord schemeC = new ConceptSchemeRecord(createDummyScheme());
        ProjectRecord record = new ProjectRecord("test-project");

        ProjectDataSet originalProject = new ProjectDataSet(record, List.of(schemeA, schemeB));
        dao.storeDataSet(originalProject);

        List<ConceptSchemeRecord> updatedSchemeList = List.of(schemeB, schemeC);
        ProjectDataSet updateProject = new ProjectDataSet(record, updatedSchemeList);

        // When
        dao.storeDataSet(updateProject);
        ProjectDataSet retrieved = dao.loadDataSet("test-project");

        // Then
        assertEquals(updatedSchemeList, retrieved.getConceptSchemes());
    }

    @Test
    void shouldDetermineThatAProjectAlreadyExists() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord scheme = new ConceptSchemeRecord(createDummyScheme());
        ProjectRecord record = new ProjectRecord("test-project");
        ProjectDataSet dataSet = new ProjectDataSet(record, List.of(scheme));
        dao.storeDataSet(dataSet);

        // When/Then
        assertTrue(dao.projectExists("test-project"));
    }

    @Test
    void shouldDetermineThatAProjectDoesNotAlreadyExist() {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);

        // When/Then
        assertFalse(dao.projectExists("test-project"));
    }

    @Test
    void shouldListAllProjects() throws SQLException {
        // Given
        ProjectDao dao = new ProjectDao(dataSource);
        ConceptSchemeRecord scheme = new ConceptSchemeRecord(createDummyScheme());

        ProjectRecord record1 = new ProjectRecord("project-1");
        ProjectDataSet dataSet1 = new ProjectDataSet(record1, List.of(scheme));
        dao.storeDataSet(dataSet1);

        ProjectRecord record2 = new ProjectRecord("project-2");
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
             var stmt = conn.prepareStatement("INSERT INTO skos_concept_scheme (uuid) VALUES (?)")) {
            stmt.setObject(1, schemeUuid, Types.OTHER);
            assumeTrue(stmt.executeUpdate() > 0);
        }
        return schemeUuid;
    }
}
