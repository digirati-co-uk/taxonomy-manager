package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.analysis.corenlp.CoreNlpWordTokenizer;
import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.analysis.index.simple.SimpleTermIndex;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.UUID;

@ApplicationScoped
public class TaxonomyIndexConfig {
    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String languageKey;

    @Produces
    @ApplicationScoped
    TermIndex<UUID> termIndex() {
        return new SimpleTermIndex<>(CoreNlpWordTokenizer.create(languageKey));
    }
}
