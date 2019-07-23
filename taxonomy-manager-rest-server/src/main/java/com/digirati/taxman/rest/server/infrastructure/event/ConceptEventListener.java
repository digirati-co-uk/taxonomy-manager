package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.rest.server.infrastructure.config.TaxonomyIndexConfig;
import com.google.common.eventbus.Subscribe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

/**
 * Listener to respond to changes in the state of a concept.
 */
@ApplicationScoped
public class ConceptEventListener {

    @Inject
    TermIndex<UUID> index;

}
