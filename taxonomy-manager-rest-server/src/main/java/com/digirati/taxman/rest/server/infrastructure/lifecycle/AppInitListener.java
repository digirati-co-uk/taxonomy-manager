package com.digirati.taxman.rest.server.infrastructure.lifecycle;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
import com.digirati.taxman.rest.server.infrastructure.config.TaxonomyIndexConfig;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
    ConceptDao conceptDao;

    @Inject
    TermIndex<UUID> termIndex;

    void onStartup(@Observes StartupEvent event) {
        try (var conceptRecords = conceptDao.loadAllRecords()) {
            var terms = new HashMap<UUID, String>();

            conceptRecords.forEach(record -> {
                var uuid = record.getUuid();
                var labelExtractor = new ConceptLabelExtractor(record);

                labelExtractor.extractTo((property, values) -> {
                    terms.put(uuid, values.get(defaultLanguageKey));
                });
            });

            logger.info("Found {} terms", terms.size());
            termIndex.addAll(terms);
        }

        logger.info("Finished building term index");
    }
}
