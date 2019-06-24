package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.ConceptSchemeMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class TaxonomyConfig {

    @Inject
    RdfModelFactory modelFactory;

    @Inject
    ConceptIdResolver conceptIdResolver;

    @Inject
    ConceptSchemeIdResolver conceptSchemeIdResolver;

    @Produces
    ConceptMapper conceptMapper() {
        return new ConceptMapper(conceptIdResolver, modelFactory);
    }

    @Produces
    ConceptSchemeMapper conceptSchemeMapper() {
        return new ConceptSchemeMapper(conceptSchemeIdResolver, conceptIdResolver, modelFactory);
    }
}
