package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.google.common.eventbus.Subscribe;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Listener to respond to changes in the state of a concept.
 */
@ApplicationScoped
public class ConceptEventListener {

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @Inject
    TermIndex<UUID> index;

    @Subscribe
    public void ConceptChangeEvent(ConceptEvent conceptEvent) {
        if (conceptEvent.getPrevious() != null)
            Remove(conceptEvent.getPrevious());

        if (conceptEvent.getConcept() != null)
            Add(conceptEvent.getConcept());
    }

    private void Add(ConceptModel concept) {
        ConsumeLabels(concept, index::add);
    }

    private void Remove(ConceptModel previous) {
        ConsumeLabels(previous, index::remove);
    }

    private void ConsumeLabels(ConceptModel concept, BiConsumer<UUID, String> consumer) {
        var labelExtractor = new ConceptLabelExtractor(concept);

        labelExtractor.extractTo((property, literal) -> {
            Collection<String> values = literal.get(defaultLanguageKey);
            values.forEach(value -> consumer.accept(concept.getUuid(), value));
        });
    }
}
