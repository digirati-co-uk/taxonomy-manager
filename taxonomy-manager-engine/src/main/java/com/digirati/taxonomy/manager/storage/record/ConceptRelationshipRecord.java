package com.digirati.taxonomy.manager.storage.record;

import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConceptRelationshipRecord {
    private final UUID source;
    private final UUID target;
    private final ConceptRelationshipType type;
    private final boolean transitive;

    private Map<String, String> preferredLabel = new HashMap<>();

    public ConceptRelationshipRecord(UUID source, UUID target, ConceptRelationshipType type, boolean transitive) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.transitive = transitive;
    }

    public UUID getSource() {
        return source;
    }

    public UUID getTarget() {
        return target;
    }

    public ConceptRelationshipType getType() {
        return type;
    }

    public boolean isTransitive() {
        return transitive;
    }

    public Map<String, String> getPreferredLabel() {
        return preferredLabel;
    }

    public void setPreferredLabel(Map<String, String> preferredLabel) {
        this.preferredLabel = preferredLabel;
    }
}
