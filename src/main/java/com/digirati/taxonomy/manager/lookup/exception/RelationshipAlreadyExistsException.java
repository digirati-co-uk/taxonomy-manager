package com.digirati.taxonomy.manager.lookup.exception;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;

public class RelationshipAlreadyExistsException extends Exception {

    public RelationshipAlreadyExistsException(ConceptSemanticRelationModel relationship) {
        super("A relationship already exists for: " + relationship);
    }
}
