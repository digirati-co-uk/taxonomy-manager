package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.digirati.taxman.common.taxonomy.ConceptRelationshipType;
import com.google.common.base.Objects;

import java.util.Map;
import java.util.UUID;

/**
 * A model of the <code>skos_concept_semantic_relation</code> table in the taxman DDL.
 *
 * <p>This record additionally models the output of the <code>get_concept_relationships(uuid)</code>
 * procedure, where a preferred label is also present.
 */
public class ConceptRelationshipRecord {
    private final UUID source;
    private final UUID target;
    private final String targetSource;
    private final ConceptRelationshipType type;
    private final boolean transitive;

    private final Map<String, String> targetPreferredLabel;

    /**
     * Create a new {@link ConceptRelationshipRecord} without a preferred label.
     *  @param source The identity of the source {@link ConceptRecord}.
     * @param target The identity of the target {@link ConceptRecord}.
     * @param targetSource
     * @param type The type of the relationship.
     * @param transitive If this relationship is transitive.
     */
    public ConceptRelationshipRecord(
            UUID source, UUID target, String targetSource, ConceptRelationshipType type, boolean transitive) {
        this(source, target, targetSource, type, transitive, Map.of());
    }

    public ConceptRelationshipRecord(
            UUID source,
            UUID target,
            String targetSource, ConceptRelationshipType type,
            boolean transitive,
            Map<String, String> targetPrefLabel) {
        this.source = source;
        this.target = target;
        this.targetSource = targetSource;
        this.type = type;
        this.transitive = transitive;
        this.targetPreferredLabel = targetPrefLabel;
    }

    /**
     * Get the identity of the source concept in this relationship.
     *
     * @return the identity of the source {@link ConceptRecord}.
     */
    public UUID getSource() {
        return source;
    }

    /**
     * Get the identity of the target concept in this relationship.
     *
     * @return the identity of the target {@link ConceptRecord}.
     */
    public UUID getTarget() {
        return target;
    }

    /**
     * Get the semantic relation type of this relationship.
     *
     * @return the semantic relation type.
     */
    public ConceptRelationshipType getType() {
        return type;
    }

    /**
     * Check if this relationship (along with its {@code} type), represent the transitive variant
     * of the relation.
     *
     * @return {@code true} iff this is a transitive relationship.
     */
    public boolean isTransitive() {
        return transitive;
    }

    /**
     * Get the preferred label of the target concept in this relationship.
     *
     * @return A plain literal representing the preferred label of the target concept.
     */
    public Map<String, String> getTargetPreferredLabel() {
        return targetPreferredLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptRelationshipRecord that = (ConceptRelationshipRecord) o;
        return transitive == that.transitive
                && Objects.equal(source, that.source)
                && Objects.equal(target, that.target)
                && type == that.type
                && Objects.equal(targetPreferredLabel, that.targetPreferredLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(source, target, type, transitive, targetPreferredLabel);
    }

    public String getTargetSource() {
        return targetSource;
    }
}
