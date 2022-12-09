package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConceptSchemeReferenceRecord {
    private UUID uuid;
    private Map<String, List<String>> title;

    @JsonCreator
    public ConceptSchemeReferenceRecord(@JsonProperty("uuid") UUID uuid, @JsonProperty("title") Map<String, List<String>> title) {
        this.uuid = uuid;
        this.title = title;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, List<String>> getTitle() {
        return title;
    }
}
