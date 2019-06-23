package com.digirati.taxman.rest.server.infrastructure.config;

import com.digirati.taxman.common.rdf.RdfModelFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class RdfConfig {
    @Produces
    public RdfModelFactory rdfModelFactory() {
        return new RdfModelFactory();
    }
}
