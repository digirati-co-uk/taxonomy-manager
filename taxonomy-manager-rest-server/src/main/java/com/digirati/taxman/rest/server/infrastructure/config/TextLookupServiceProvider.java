package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.taxonomy.Term;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxonomy.manager.lookup.TextLookupService;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class TextLookupServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(TextLookupServiceProvider.class);

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @ConfigProperty(name = "taxman.analysis.default-lang.name", defaultValue = "english")
    String defaultLanguageName;

    @Inject
    ConceptDao conceptDao;

    private TextLookupService textLookupService;

    void onStartup(@Observes StartupEvent event) {
        try (var conceptRecords = conceptDao.loadAllRecords()) {
            var terms = conceptRecords.map(c -> new Term(c.getUuid(), c.getLabels(defaultLanguageKey)));
            var serviceFuture = TextLookupService.initialiseLookupService(
                    terms,
                    defaultLanguageKey,
                    defaultLanguageName
            );

            textLookupService = serviceFuture.get(10, TimeUnit.SECONDS);
            logger.info("Finished building TextLookupService");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Text Lookup Service was interrupted during initialization", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new WebApplicationException("Text Lookup Service failed to initialize", e);
        }
    }

    public TextLookupService getTextLookupService() {
        return textLookupService;
    }
}
