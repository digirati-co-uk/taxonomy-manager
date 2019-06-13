package com.digirati.taxonomy.manager.lookup.exception;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;

public class SkosPersistenceException extends Exception {

    protected SkosPersistenceException(String message) {
        super(message);
    }

    protected SkosPersistenceException(String message, Throwable t) {
        super(message, t);
    }

    public static SkosPersistenceException conceptAlreadyExists(String id) {
        return new SkosPersistenceException("Concept with ID=" + id + " already exists");
    }

    public static SkosPersistenceException conceptNotFound(String id) {
        return new SkosPersistenceException("Could not find concept with ID=" + id);
    }

    public static SkosPersistenceException conceptSchemeAlreadyExists(String id) {
        return new SkosPersistenceException("Concept scheme with ID=" + id + " already exists");
    }

    public static SkosPersistenceException conceptSchemeNotFound(String id) {
        return new SkosPersistenceException("Could not find concept scheme with ID=" + id);
    }

    public static SkosPersistenceException relationshipAlreadyExists(
            ConceptSemanticRelationModel relationship) {
        return new SkosPersistenceException("A relationship already exists for " + relationship);
    }

    public static SkosPersistenceException relationshipNotFound(String sourceId, String targetId) {
        return new SkosPersistenceException(
                "Relationship between " + sourceId + " and " + targetId + " was not found");
    }

    public static SkosPersistenceException relationshipNotFound(
            String sourceId, String targetId, Throwable t) {
        return new SkosPersistenceException(
                "Relationship between " + sourceId + " and " + targetId + " was not found", t);
    }

    public static SkosPersistenceException relationshipNotFound(
            ConceptSemanticRelationModel relationship) {
        return relationshipNotFound(relationship.getSourceId(), relationship.getTargetId());
    }

    public static SkosPersistenceException unableToCreateConcept(String id, Throwable t) {
        return new SkosPersistenceException("Unable to create concept with ID=" + id, t);
    }

    public static SkosPersistenceException unableToCreateConceptScheme(String id, Throwable t) {
        return new SkosPersistenceException("Unable to create concept scheme with ID=" + id, t);
    }

    public static SkosPersistenceException unableToCreateRelationship(
            ConceptSemanticRelationModel relationship) {
        return new SkosPersistenceException("Unable to create relationship: " + relationship);
    }

    public static SkosPersistenceException unableToCreateRelationship(
            ConceptSemanticRelationModel relationship, Throwable t) {
        return new SkosPersistenceException("Unable to create relationship: " + relationship, t);
    }

    public static SkosPersistenceException unableToGetRelationships(String id) {
        return new SkosPersistenceException("Unable to get relationships for " + id);
    }

    public static SkosPersistenceException unableToUpdateConcept(String id, Throwable t) {
        return new SkosPersistenceException("Unable to update concept with ID=" + id, t);
    }

    public static SkosPersistenceException unableToUpdateConceptScheme(String id, Throwable t) {
        return new SkosPersistenceException("Unable to update concept scheme with ID=" + id, t);
    }

    public static SkosPersistenceException unableToUpdateRelationship(
            ConceptSemanticRelationModel relationship) {
        return new SkosPersistenceException("Unable to update relationship: " + relationship);
    }

    public static SkosPersistenceException unableToUpdateRelationship(
            ConceptSemanticRelationModel relationship, Throwable t) {
        return new SkosPersistenceException("Unable to update relationship: " + relationship, t);
    }
}
