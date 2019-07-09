package com.digirati.taxman.rest.server.infrastructure.lifecycle;

import com.digirati.taxonomy.manager.lookup.TextLookupService;
import io.quarkus.runtime.StartupEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class AnalyzerInitListener {
    private static final Log logger = LogFactory.getLog(AnalyzerInitListener.class);

    @Inject
    TextLookupService service;

    void onStartup(@Observes StartupEvent event) {
        logger.info("Finished building TextLookupService");
    }

}
