package com.digirati.taxman.rest.server;

import com.digirati.taxman.rest.analysis.TextAnalysisInput;
import com.digirati.taxman.rest.analysis.TextAnalysisResource;
import com.digirati.taxman.rest.server.analysis.TextAnalyzer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class ServerTextAnalysisResource implements TextAnalysisResource {

    @Inject
    TextAnalyzer textAnalyzer;

    @Override
    public Response analyze(@Valid TextAnalysisInput input) {
        return Response.ok(textAnalyzer.tagDocument(input)).build();
    }
}
