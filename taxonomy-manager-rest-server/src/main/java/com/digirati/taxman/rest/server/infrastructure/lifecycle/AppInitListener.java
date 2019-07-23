package com.digirati.taxman.rest.server.infrastructure.lifecycle;

import com.digirati.taxman.rest.server.infrastructure.config.TaxonomyIndexConfig;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.analysis.index.TermIndex;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.UUID;

@ApplicationScoped
public class AppInitListener {
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyIndexConfig.class);

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @ConfigProperty(name = "taxman.analysis.default-lang.name", defaultValue = "english")
    String defaultLanguageName;

    @Inject
    Flyway flyway;

    @Inject
    ConceptDao conceptDao;

    @Inject
    TermIndex<UUID> termIndex;

    void onStartup(@Observes StartupEvent event) {
        // We need to run migrations first so `loadAllRecords()` doesn't attempt to race against flyway,
        // potentially executing queries against a database that hasn't been setup yet.

        flyway.baseline();
        flyway.migrate();

        try (var conceptRecords = conceptDao.loadAllRecords()) {
            var terms = new HashMap<UUID, String>();

            conceptRecords.forEach(record -> {
                var uuid = record.getUuid();

                for (var label : record.getLabels(defaultLanguageKey)) {
                    terms.put(uuid, label);
                }
            });

            termIndex.addAll(terms);
        }

        logger.info("Finished building term index");
    }
}
