package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordTokenizer;
import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.analysis.search.NaiveSearchStrategy;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import java.util.UUID;

@ApplicationScoped
public class TaxonomyIndexConfig {
    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String languageKey;

    @Produces
    @Singleton
    public TermIndex<String, UUID> termIndex() {
        return new TermIndex<String, UUID>(CoreNlpWordTokenizer.create(languageKey), new NaiveSearchStrategy<>());
    }
}
