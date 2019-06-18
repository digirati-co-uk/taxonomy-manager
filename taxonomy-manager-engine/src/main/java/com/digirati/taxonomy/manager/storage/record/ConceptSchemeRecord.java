package com.digirati.taxonomy.manager.storage.record;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConceptSchemeRecord {
    private final UUID uuid;
    private Map<String, String> title = new HashMap<>();

    public ConceptSchemeRecord(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }
}
