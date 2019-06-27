package com.digirati.taxman.rest.server.infrastructure.lifecycle;

import io.quarkus.runtime.StartupEvent;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class DatabaseInitListener {

    @Inject Flyway flyway;

    void onStart(@Observes StartupEvent event) {
        flyway.baseline();
        flyway.migrate();
    }
}
