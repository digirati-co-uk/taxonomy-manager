package com.digirati.taxman.rest.server.infrastructure.event;

import com.digirati.taxman.common.taxonomy.ConceptModel;

import java.util.Collection;
import java.util.List;

/**
 * Event to be sent when the state of a concept changes.
 */
public class ConceptEvent {

    private enum Type {
        CREATED,
        UPDATED,
        IMPORT
    }

    private final Type type;

    private List<ConceptModel> concepts;

    public static ConceptEvent created(ConceptModel concept) {
        return new ConceptEvent(Type.CREATED, concept);
    }

    public static ConceptEvent updated(ConceptModel concept) {
        return new ConceptEvent(Type.UPDATED, concept);
    }

    public static ConceptEvent importConcepts(Collection<ConceptModel> concepts) {
        return new ConceptEvent(Type.IMPORT, List.copyOf(concepts));
    }

    private ConceptEvent(Type type, ConceptModel concept) {
        this.type = type;
        this.concepts = List.of(concept);
    }

    private ConceptEvent(Type type, List<ConceptModel> concepts) {
        this.type = type;
        this.concepts = concepts;
    }

    public ConceptModel getConcept() {
        return concepts.get(0);
    }

    public List<ConceptModel> getConcepts() {
        return concepts;
    }

    public boolean isNew() {
        return Type.CREATED == type;
    }

    public boolean isImport() {
        return Type.IMPORT == type;
    }
}
