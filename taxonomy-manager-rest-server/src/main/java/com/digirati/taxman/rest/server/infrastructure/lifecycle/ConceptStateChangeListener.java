package com.digirati.taxman.rest.server.infrastructure.lifecycle;

import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.server.taxonomy.ConceptExtractionService;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Listens for state changes on concepts and informs the engine of the change.
 */
@ApplicationScoped
public class ConceptStateChangeListener {

    @Inject
    ConceptExtractionService conceptExtractionService;

    @Incoming("created-concepts")
    public void onConceptCreated(ConceptModel concept) {
        conceptExtractionService.getTextLookupService().addConcept(concept);
    }

    @Incoming("updated-concepts")
    public void onConceptUpdated(ConceptModel concept) {
        conceptExtractionService.getTextLookupService().updateConcept(concept);
    }
}
