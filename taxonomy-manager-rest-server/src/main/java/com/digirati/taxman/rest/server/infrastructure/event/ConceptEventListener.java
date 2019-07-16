package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxonomy.manager.lookup.TextLookupService;
import com.google.common.eventbus.Subscribe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Listener to respond to changes in the state of a concept.
 */
@ApplicationScoped
public class ConceptEventListener {

    @Inject
    TextLookupService lookupService;

    @Subscribe
    public void onEvent(ConceptEvent event) {
        if (event.isImport()) {
            lookupService.addConcepts(event.getConcepts());
        } else if (event.isNew()) {
            lookupService.addConcept(event.getConcept());
        } else {
            lookupService.updateConcept(event.getConcept());
        }
    }
}
