package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

@RdfType("http://www.w3.org/2004/02/skos/core#Collection")
@RdfContext({"skos=" + SKOS.uri, "dcterms=" + DCTerms.NS})
public class CollectionModel implements RdfModel {

    private final Resource resource;

    @RdfConstructor
    public CollectionModel(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return resource;
    }
}
