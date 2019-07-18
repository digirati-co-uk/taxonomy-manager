package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.rest.server.infrastructure.config.TextLookupServiceProvider;
import com.google.common.eventbus.Subscribe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Listener to respond to changes in the state of a concept.
 */
@ApplicationScoped
public class ConceptEventListener {

    @Inject
    TextLookupServiceProvider lookupService;

    @Subscribe
    public void onEvent(ConceptEvent event) {
        if (event.isImport()) {
            lookupService.getTextLookupService().addConcepts(event.getConcepts());
        } else if (event.isNew()) {
            lookupService.getTextLookupService().addConcept(event.getConcept());
        } else {
            lookupService.getTextLookupService().updateConcept(event.getConcept());
        }
    }
}
