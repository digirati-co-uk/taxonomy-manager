package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.common.taxonomy.ConceptModel;

import java.io.Serializable;

/**
 * Event to be sent when the state of a concept changes.
 */
public class ConceptEvent implements Serializable {

    private final Type type;
    private final ConceptModel previous;
    private final ConceptModel concept;

    private ConceptEvent(Type type, ConceptModel concept, ConceptModel previous) {
        this.type = type;
        this.concept = concept;
        this.previous = previous;
    }

    public static ConceptEvent deleted(ConceptModel concept) {
        return new ConceptEvent(Type.DELETED, null, concept);
    }

    public static ConceptEvent created(ConceptModel concept) {
        return new ConceptEvent(Type.CREATED, concept, null);
    }

    public static ConceptEvent updated(ConceptModel concept, ConceptModel existing) {
        return new ConceptEvent(
                existing == null ? Type.CREATED : Type.UPDATED,
                concept, existing);
    }

    public Type getType() {
        return type;
    }

    public ConceptModel getConcept() {
        return concept;
    }

    public ConceptModel getPrevious() {
        return previous;
    }

    public boolean isNew() {
        return Type.CREATED == type;
    }

    public enum Type {
        CREATED,
        UPDATED,
        DELETED
    }
}
