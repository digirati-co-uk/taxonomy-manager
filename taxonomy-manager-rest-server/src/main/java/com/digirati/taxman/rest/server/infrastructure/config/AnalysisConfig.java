package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.Term;
import com.digirati.taxman.rest.server.analysis.TextAnalyzer;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxman.rest.server.taxonomy.storage.ConceptDao;
import com.digirati.taxonomy.manager.lookup.TextLookupService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class AnalysisConfig {

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @ConfigProperty(name = "taxman.analysis.default-lang.name", defaultValue = "english")
    String defaultLanguageName;

    @Inject
    ConceptDao conceptDao;

    @Produces
    TextLookupService textLookupService() {
        try (var conceptRecords = conceptDao.loadAllRecords()) {
            var terms = conceptRecords.map(c -> new Term(c.getUuid(), c.getLabels(defaultLanguageKey)));
            var serviceFuture = TextLookupService.initialiseLookupService(
                    terms,
                    defaultLanguageKey,
                    defaultLanguageName
            );

            return serviceFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebApplicationException("Text Lookup Service was interrupted during initialization", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new WebApplicationException("Text Lookup Service failed to initialize", e);
        }
    }

    @Produces
    TextAnalyzer textAnalyzer(TextLookupService lookupService, RdfModelFactory modelFactory, ConceptModelRepository concepts) {
        return new TextAnalyzer(lookupService, modelFactory, concepts);
    }

}
