package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.taxonomy.ConceptLabelExtractor;
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
        UUID uuid = conceptEvent.getConcept().getUuid();

        BiConsumer<UUID, String> consumer = null;
        switch (conceptEvent.getType()) {

            case CREATED:
                //consumer = index::add;
                break;
            case UPDATED:
                // ???
                break;
            case DELETED:
                consumer = index::remove;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + conceptEvent.getType());
        }

        if (consumer == null) return;
        final BiConsumer<UUID, String> finalConsumer = consumer;

        var labelExtractor = new ConceptLabelExtractor(conceptEvent.getConcept());
        labelExtractor.extractTo((property, literal) -> {
            Collection<String> values = literal.get(defaultLanguageKey);
            values.forEach(value -> finalConsumer.accept(uuid, value));
        });
    }
}
