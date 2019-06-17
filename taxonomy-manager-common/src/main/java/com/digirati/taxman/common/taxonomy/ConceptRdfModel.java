package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import org.apache.jena.rdf.model.Resource;

@RdfType("http://www.w3.org/2004/02/skos/core#Concept")
public final class ConceptRdfModel implements RdfModel  {

    private final Resource resource;

    @RdfConstructor
    public ConceptRdfModel(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return resource;
    }
}
