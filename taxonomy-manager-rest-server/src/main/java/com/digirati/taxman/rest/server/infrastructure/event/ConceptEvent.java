package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.common.taxonomy.ConceptModel;

/**
 * Event to be sent when the state of a concept changes.
 */
public class ConceptEvent {

    public static ConceptEvent deleted(ConceptModel concept) {
        return new ConceptEvent(Type.DELETED, concept);
    }

    private final Type type;

    private final ConceptModel concept;

    public static ConceptEvent created(ConceptModel concept) {
        return new ConceptEvent(Type.CREATED, concept);
    }

    public static ConceptEvent updated(ConceptModel concept) {
        return new ConceptEvent(Type.UPDATED, concept);
    }

    public Type getType() {
        return type;
    }

    private ConceptEvent(Type type, ConceptModel concept) {
        this.type = type;
        this.concept = concept;
    }

    public ConceptModel getConcept() {
        return concept;
    }

    public boolean isNew() {
        return Type.CREATED == type;
    }
}
