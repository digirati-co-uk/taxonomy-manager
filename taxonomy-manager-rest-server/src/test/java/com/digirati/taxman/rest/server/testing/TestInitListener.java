package com.digirati.taxman.rest.server.testing;

import io.quarkus.runtime.StartupEvent;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.sql.SQLException;

@ApplicationScoped
@Priority(0)
public class TestInitListener {

    private static final Logger logger = LoggerFactory.getLogger(TestInitListener.class);

    @Inject
    Flyway flyway;

    void onStartup(@Observes StartupEvent event) throws SQLException {
        try (var conn = flyway.getDataSource().getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DROP SCHEMA public CASCADE");
            stmt.execute("CREATE SCHEMA public");
        }

        flyway.baseline();
        flyway.migrate();

        logger.info("Re-initialized database for testing");
    }
}
