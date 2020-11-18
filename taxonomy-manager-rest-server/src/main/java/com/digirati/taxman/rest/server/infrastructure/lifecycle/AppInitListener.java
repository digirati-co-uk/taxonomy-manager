package com.digirati.taxman.rest.server.infrastructure.lifecycle;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
import com.digirati.taxman.rest.server.infrastructure.config.TaxonomyIndexConfig;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AppInitListener {
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyIndexConfig.class);

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @ConfigProperty(name = "taxman.analysis.default-lang.name", defaultValue = "english")
    String defaultLanguageName;

    @Inject
    ConceptDao conceptDao;

    @Inject
    TermIndex<String, UUID> termIndex;

    @Inject
    Flyway flyway;

    void onStartup(@Observes StartupEvent event) {
        flyway.baseline();
        flyway.migrate();

        logger.info("Searching for terms");
        try (var conceptRecords = conceptDao.loadAllRecords()) {
            var projectConceptRecords = conceptRecords.collect(Collectors.groupingBy(ConceptRecord::getProjectId));

            projectConceptRecords.forEach((project, records) -> {
                records.forEach(record -> {
                    var uuid = record.getUuid();
                    var labelExtractor = new ConceptLabelExtractor(record);

                    labelExtractor.extractTo((property, literal) -> {
                        Collection<String> values = literal.get(defaultLanguageKey);
                        values.forEach(value -> termIndex.add(project, uuid, value));
                    });
                });
            });
        }

        logger.info("Finished building term index");
    }
}
