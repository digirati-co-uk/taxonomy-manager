package com.digirati.taxonomy.manager.storage.record;

import java.util.Map;
import java.util.UUID;

public class ConceptReference {
    private final UUID target;
    private final Map<String, String> preferredLabel;

    public ConceptReference(UUID target, Map<String, String> preferredLabel) {
        this.target = target;
        this.preferredLabel = preferredLabel;
    }

    public UUID getTarget() {
        return target;
    }

    public Map<String, String> getPreferredLabel() {
        return preferredLabel;
    }
}
