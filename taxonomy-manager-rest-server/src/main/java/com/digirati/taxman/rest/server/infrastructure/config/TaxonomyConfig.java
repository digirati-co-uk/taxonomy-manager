package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ConceptSchemeIdResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.CollectionUriResolver;
import com.digirati.taxman.rest.server.taxonomy.identity.ProjectIdResolver;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectListingMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.ProjectMapper;
import com.digirati.taxman.rest.server.taxonomy.mapper.SearchResultsMapper;
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
    CollectionUriResolver collectionUriResolver;

    @Inject
    ConceptSchemeIdResolver conceptSchemeIdResolver;

    @Inject
    ProjectIdResolver projectIdResolver;

    @Produces
    ConceptMapper conceptMapper() {
        return new ConceptMapper(conceptIdResolver, modelFactory);
    }

    @Produces
    SearchResultsMapper searchResultsMapper() {
        return new SearchResultsMapper(conceptIdResolver, collectionUriResolver, modelFactory);
    }

    @Produces
    ConceptSchemeMapper conceptSchemeMapper() {
        return new ConceptSchemeMapper(conceptSchemeIdResolver, conceptIdResolver, modelFactory, projectIdResolver);
    }

    @Produces
    ProjectMapper projectMapper() {
        return new ProjectMapper(projectIdResolver, conceptSchemeIdResolver, modelFactory);
    }

    @Produces
    ProjectListingMapper projectListingMapper() {
        return new ProjectListingMapper(projectIdResolver, collectionUriResolver, modelFactory);
    }
}
