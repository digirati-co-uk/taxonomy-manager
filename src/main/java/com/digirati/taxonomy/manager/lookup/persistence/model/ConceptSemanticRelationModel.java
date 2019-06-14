package com.digirati.taxonomy.manager.lookup.persistence.model;

import com.google.common.base.MoreObjects;
import org.apache.jena.rdf.model.Property;

import java.util.Objects;

/** Models a relationship between two SKOS entities. */
public class ConceptSemanticRelationModel {

    private final String sourceId;

    private final String targetId;

    private final SemanticRelationType relation;

    private final boolean transitive;

    public ConceptSemanticRelationModel(
            String sourceId, String targetId, SemanticRelationType relation, boolean transitive) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.relation = relation;
        this.transitive = transitive;
    }

    public SemanticRelationType getRelation() {
        return relation;
    }

    public Property getRelationPredicate() {
        return relation.getProperty(transitive);
    }

    public boolean isTransitive() {
        return transitive;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sourceId", sourceId)
                .add("targetId", targetId)
                .add("relation", relation.name())
                .add("transitive", transitive)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptSemanticRelationModel that = (ConceptSemanticRelationModel) o;
        return transitive == that.transitive
                && relation == that.relation
                && Objects.equals(sourceId, that.sourceId)
                && Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation, transitive, sourceId, targetId);
    }
}
