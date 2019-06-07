package com.digirati.taxonomy.manager.lookup.exception;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;

public class UnableToCreateRelationshipException extends Exception {

    public UnableToCreateRelationshipException(ConceptSemanticRelationModel relationship) {
        super("Unable to create relationship: " + relationship);
    }

    public UnableToCreateRelationshipException(
            ConceptSemanticRelationModel relationship, Exception e) {
        super("Unable to create relationship: " + relationship, e);
    }
}
