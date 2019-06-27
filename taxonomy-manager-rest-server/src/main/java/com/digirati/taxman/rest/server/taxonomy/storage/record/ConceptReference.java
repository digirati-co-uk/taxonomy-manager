package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.google.common.base.Objects;

import java.util.Map;
import java.util.UUID;


/**
 * A reference to a {@link com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet},
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptReference that = (ConceptReference) o;
        return Objects.equal(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(target);
    }
}
