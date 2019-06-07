package com.digirati.taxonomy.manager.lookup.exception;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;

public class UnableToUpdateRelationshipException extends Exception {

    public UnableToUpdateRelationshipException(ConceptSemanticRelationModel relationship) {
        super("Unable to update relationship: " + relationship);
    }

    public UnableToUpdateRelationshipException(
            ConceptSemanticRelationModel relationship, Exception e) {
        super("Unable to update relationship: " + relationship, e);
    }
}
