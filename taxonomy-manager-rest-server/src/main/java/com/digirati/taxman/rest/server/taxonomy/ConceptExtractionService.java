package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.Term;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxonomy.manager.lookup.TextLookupService;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@ApplicationScoped
public class ConceptExtractionService {

    @ConfigProperty(name = "taxman.analysis.default-lang.key")
    String defaultLanguageKey;

    @ConfigProperty(name = "taxman.analysis.default-lang.name")
    String defaultLanguageName = "english";

    @Inject
    ConceptDao conceptDao;

    private TextLookupService textLookupService;

    void onStartup(@Observes StartupEvent event) {
        Stream<Term> concepts = conceptDao.loadAllRecords()
                .map(concept -> new Term(concept, defaultLanguageKey));

        try {
            textLookupService = TextLookupService.initialiseLookupService(concepts,
                    defaultLanguageKey, defaultLanguageName).get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Unable to initialise lookup service", e);

        } catch (ExecutionException e) {
            throw new WebApplicationException("Unable to initialise lookup service", e);
        }
    }

    public TextLookupService getTextLookupService() {
        return textLookupService;
    }
}
