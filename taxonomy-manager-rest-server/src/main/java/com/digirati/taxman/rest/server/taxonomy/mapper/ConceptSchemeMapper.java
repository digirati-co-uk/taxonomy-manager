package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;

public class ConceptSchemeMapper {

    private final ConceptSchemeIdResolver idResolver;
    private final RdfModelFactory modelFactory;

    public ConceptSchemeMapper(ConceptSchemeIdResolver idResolver, RdfModelFactory modelFactory) {
        this.idResolver = idResolver;
        this.modelFactory = modelFactory;
    }
}
