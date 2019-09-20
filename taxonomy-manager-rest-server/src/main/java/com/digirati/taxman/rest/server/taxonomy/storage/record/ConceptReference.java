package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 * A reference to a {@link com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet},
 * with only an identifier and a display label.
 */
public class ConceptReference {
    private final UUID target;
    private final String targetSource;
    private final Multimap<String, String> preferredLabel;

    public ConceptReference(UUID target, String source, Multimap<String, String> preferredLabel) {
        this.target = target;
        this.targetSource = source;
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

    public Multimap<String, String> getPreferredLabel() {
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

    public Optional<String> getSource() {
        return Optional.ofNullable(targetSource);
    }
}
