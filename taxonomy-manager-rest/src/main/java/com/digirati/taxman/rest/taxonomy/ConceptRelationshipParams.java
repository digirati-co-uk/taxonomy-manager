package com.digirati.taxman.rest.taxonomy;

import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;

import javax.ws.rs.QueryParam;

public class ConceptRelationshipParams {

    @QueryParam("depth")
    private int depth;

    @QueryParam("type")
    private String type;

    public int getDepth() {
        return depth;
    }

    public ConceptRelationshipType getType() {
        return ConceptRelationshipType.valueOf(type.toUpperCase());
    }
}
