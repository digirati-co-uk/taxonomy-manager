package com.digirati.taxman.common.rdf;

public interface PersistentProjectScopedModel extends PersistentModel {
    String getProjectId();

    void setProjectId(String id);
}
