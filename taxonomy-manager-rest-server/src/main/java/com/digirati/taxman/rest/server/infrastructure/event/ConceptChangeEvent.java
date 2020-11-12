package com.digirati.taxman.rest.server.infrastructure.event;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class ConceptChangeEvent implements Serializable {
    private final UUID uuid;
    private final String projectId;
    private final List<String> added;
    private final List<String> removed;

    public ConceptChangeEvent(UUID uuid, String projectId, List<String> added, List<String> removed) {
        this.uuid = uuid;
        this.projectId = projectId;
        this.added = added;
        this.removed = removed;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getProjectId() {
        return projectId;
    }

    public List<String> getAdded() {
        return added;
    }

    public List<String> getRemoved() {
        return removed;
    }
}
