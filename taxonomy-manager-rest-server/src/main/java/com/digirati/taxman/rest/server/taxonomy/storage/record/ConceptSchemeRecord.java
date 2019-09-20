package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ConceptSchemeRecord {
    private final UUID uuid;
    private Multimap<String, String> title = ArrayListMultimap.create();
    private String source;

    public ConceptSchemeRecord(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Multimap<String, String> getTitle() {
        return title;
    }

    public void setTitle(Multimap<String, String> title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptSchemeRecord that = (ConceptSchemeRecord) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
