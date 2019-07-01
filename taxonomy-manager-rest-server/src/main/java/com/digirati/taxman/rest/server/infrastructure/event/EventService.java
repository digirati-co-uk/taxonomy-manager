package com.digirati.taxman.rest.server.infrastructure.event;

import com.google.common.eventbus.EventBus;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Wrapper around Guava's {@link EventBus}.
 */
@ApplicationScoped
public class EventService {

    @Inject
    ConceptEventListener conceptEventListener;

    private EventBus eventBus;

    void onStartup(@Observes StartupEvent startupEvent) {
        eventBus = new EventBus();
        eventBus.register(conceptEventListener);
    }

    public void send(Object event) {
        eventBus.post(event);
    }
}
