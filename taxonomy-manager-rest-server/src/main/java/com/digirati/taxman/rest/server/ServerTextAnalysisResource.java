package com.digirati.taxman.rest.server;

import com.digirati.taxman.rest.analysis.TextAnalysisInput;
import com.digirati.taxman.rest.analysis.TextAnalysisResource;
import com.digirati.taxman.rest.server.analysis.TextAnalyzer;
import com.digirati.taxman.rest.taxonomy.ConceptSchemePath;
import com.digirati.taxman.rest.taxonomy.ProjectPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.util.Optional;

@ApplicationScoped
public class ServerTextAnalysisResource implements TextAnalysisResource {

    @Inject
    TextAnalyzer textAnalyzer;

    @Override
    public Response analyze(@Valid TextAnalysisInput input) {
        return Response.ok(textAnalyzer.tagDocument(input, Optional.empty(), Optional.empty())).build();
    }

    @Override
    public Response analyze(ProjectPath projectPath, @Valid TextAnalysisInput input) {
        return Response.ok(textAnalyzer.tagDocument(input,
                Optional.of(projectPath.getProjectSlug()),
                Optional.empty())).build();
    }

    @Override
    public Response analyze(ConceptSchemePath schemePath, @Valid TextAnalysisInput input) {
        return Response.ok(textAnalyzer.tagDocument(input,
                Optional.of(schemePath.getProjectSlug()),
                Optional.of(schemePath.getSchemeUuid()))).build();
    }
}
