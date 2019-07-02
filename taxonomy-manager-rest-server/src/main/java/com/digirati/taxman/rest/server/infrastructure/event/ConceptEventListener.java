package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.rest.server.taxonomy.ConceptExtractionService;
import com.google.common.eventbus.Subscribe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Listener to respond to changes in the state of a concept.
 */
@ApplicationScoped
public class ConceptEventListener {

    @Inject
    ConceptExtractionService conceptExtractionService;

    @Subscribe
    public void onEvent(ConceptEvent event) {
        if (event.isNew()) {
            conceptExtractionService.getTextLookupService().addConcept(event.getConcept());
        } else {
            conceptExtractionService.getTextLookupService().updateConcept(event.getConcept());
        }
    }
}
