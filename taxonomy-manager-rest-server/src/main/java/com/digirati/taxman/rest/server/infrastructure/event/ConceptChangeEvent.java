package com.digirati.taxman.rest.server.infrastructure.event;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class ConceptChangeEvent implements Serializable {
    private final UUID uuid;
    private final List<String> added;
    private final List<String> removed;

    public ConceptChangeEvent(UUID uuid, List<String> added, List<String> removed) {
        this.uuid = uuid;
        this.added = added;
        this.removed = removed;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<String> getAdded() {
        return added;
    }

    public List<String> getRemoved() {
        return removed;
    }
}
