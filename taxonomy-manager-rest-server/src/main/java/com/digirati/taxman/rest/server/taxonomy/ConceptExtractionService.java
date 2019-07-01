package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.Concept;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxonomy.manager.lookup.TextLookupService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
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

    @Inject
    ConceptMapper conceptMapper;

    private TextLookupService textLookupService;

    @PostConstruct
    public void initialiseExtractionService() {
        Stream<? extends Concept> concepts = conceptDao.loadAllRecords();

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
