package com.digirati.taxman.rest.server.testing;

import com.digirati.taxman.rest.server.testing.annotation.TestDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Savepoint;

/**
 * A JUnit 5 extension that spins up a PostgreSQL test container and applies migrations with flyway,
 * rolling back any changes after each test run.
 */
public class DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("db-test");
    private static final String CONNECTION_KEY = "connection";
    private static final String CONTAINER_KEY = "container";
    private static final String SAVEPOINT_KEY = "savepoint";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            var store = context.getStore(NAMESPACE);
            var container = new PostgreSQLContainer<>("postgres:11");

            container.start();

            var connection = DriverManager.getConnection(
                    container.getJdbcUrl(),
                    container.getUsername(),
                    container.getPassword());

            var dataSource = new SingleConnectionDataSource(connection, true);
            var flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("db")
                    .baselineOnMigrate(true)
                    .repeatableSqlMigrationPrefix("R")
                    .load();

            flyway.migrate();

            store.put(CONTAINER_KEY, container);
            store.put(CONNECTION_KEY, connection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        var store = context.getStore(NAMESPACE);
        var connection = store.get(CONNECTION_KEY, Connection.class);
        connection.setAutoCommit(false);

        var savepoint = connection.setSavepoint();
        var dataSource = new SingleConnectionDataSource(connection, true);

        Object testInstance = context.getRequiredTestInstance();
        Field[] testInstanceFields = context.getRequiredTestClass().getDeclaredFields();

        for (var field : testInstanceFields) {
            var testDsMarker = field.getAnnotation(TestDataSource.class);
            if (testDsMarker != null) {
                field.setAccessible(true);
                field.set(testInstance, dataSource);
            }
        }

        store.put(SAVEPOINT_KEY, savepoint);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var store = context.getStore(NAMESPACE);
        var connection = store.get(CONNECTION_KEY, Connection.class);
        var savepoint = store.get(SAVEPOINT_KEY, Savepoint.class);

        connection.rollback(savepoint);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        var store = context.getStore(NAMESPACE);
        var connection = store.get(CONNECTION_KEY, Connection.class);
        var container = store.get(CONTAINER_KEY, PostgreSQLContainer.class);

        connection.close();
        container.stop();
    }
}
