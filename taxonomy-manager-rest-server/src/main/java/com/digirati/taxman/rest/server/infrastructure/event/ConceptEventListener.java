package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.google.common.eventbus.Subscribe;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

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

    @Incoming("event-sink")
    public void handle(ConceptEvent event) {
        ConceptModel previous = event.getPrevious();
        if (previous != null) {
            remove(previous);
        }

        ConceptModel current = event.getConcept();
        if (current != null) {
            add(current);
        }
    }

    @ConfigProperty(name = "taxman.analysis.default-lang.key", defaultValue = "en")
    String defaultLanguageKey;

    @Inject
    TermIndex<UUID> index;

    private void add(ConceptModel concept) {
        consumeLabels(concept, index::add);
    }

    private void remove(ConceptModel previous) {
        consumeLabels(previous, index::remove);
    }

    private void consumeLabels(ConceptModel concept, BiConsumer<UUID, String> consumer) {
        var labelExtractor = new ConceptLabelExtractor(concept);

        labelExtractor.extractTo((property, literal) -> {
            Collection<String> values = literal.get(defaultLanguageKey);
            values.forEach(value -> consumer.accept(concept.getUuid(), value));
        });
    }
}
