package com.digirati.taxonomy.manager.storage.record;

import java.util.Map;
import java.util.UUID;


/**
 * A reference to a {@link com.digirati.taxonomy.manager.storage.ConceptDataSet},
 * with only an identifier and a display label.
 */
public class ConceptReference {
    private final UUID target;
    private final Map<String, String> preferredLabel;

    public ConceptReference(UUID target, Map<String, String> preferredLabel) {
        this.target = target;
        this.preferredLabel = preferredLabel;
    }

    /**
     * Get the identifier of the referenced concept.
     *
     * @return the referenced concept identifier.
     */
    public UUID getId() {
        return target;
    }

    public Map<String, String> getPreferredLabel() {
        return preferredLabel;
    }
}
