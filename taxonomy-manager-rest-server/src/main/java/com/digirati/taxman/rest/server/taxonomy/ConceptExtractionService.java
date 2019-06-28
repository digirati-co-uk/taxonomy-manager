package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet;
import com.digirati.taxonomy.manager.lookup.TextLookupService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@ApplicationScoped
public class ConceptExtractionService {

    // TODO make these configurable
    private static final String DEFAULT_LANGUAGE_KEY = "en";
    private static final String DEFAULT_LANGUAGE_NAME = "english";

    @Inject
    ConceptDao conceptDao;

    @Inject
    ConceptMapper conceptMapper;

    private TextLookupService textLookupService;

    public ConceptExtractionService() {
        initialiseExtractionService(DEFAULT_LANGUAGE_KEY, DEFAULT_LANGUAGE_NAME);
    }

    public void initialiseExtractionService(String languageKey, String languageName) {
        Stream<ConceptModel> concepts = conceptDao.loadAllRecords().map(record -> {
            try {
                return conceptMapper.map(new ConceptDataSet(record));
            } catch (RdfModelException e) {
                throw new WebApplicationException(e);
            }
        });

        try {
            textLookupService =
                    TextLookupService.initialiseLookupService(concepts, languageKey, languageName).get();

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
