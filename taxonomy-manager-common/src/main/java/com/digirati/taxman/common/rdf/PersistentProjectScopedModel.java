package com.digirati.taxman.common.rdf;

import java.util.UUID;

public interface PersistentProjectScopedModel extends PersistentModel {
    UUID getProjectId();

    void setProjectId(UUID uuid);
}
