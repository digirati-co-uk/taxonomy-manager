package com.digirati.taxonomy.manager.lookup.exception;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;

public class RelationshipNotFoundException extends Exception {

    public RelationshipNotFoundException(ConceptSemanticRelationModel relationship) {
        this(relationship.getSourceIri(), relationship.getTargetIri());
    }

    public RelationshipNotFoundException(String sourceIri, String targetIri) {
        super("Relationship between " + sourceIri + " and " + targetIri + " was not found.");
    }

    public RelationshipNotFoundException(String sourceIri, String targetIri, Exception e) {
        super("Relationship between " + sourceIri + " and " + targetIri + " was not found.", e);
    }
}
