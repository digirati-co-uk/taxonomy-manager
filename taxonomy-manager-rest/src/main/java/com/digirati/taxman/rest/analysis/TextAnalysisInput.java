package com.digirati.taxman.rest.analysis;

import java.util.Optional;

public class TextAnalysisInput {
    private String text;
    private String projectId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Optional<String> getProjectId() {
        return Optional.ofNullable(projectId);
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
