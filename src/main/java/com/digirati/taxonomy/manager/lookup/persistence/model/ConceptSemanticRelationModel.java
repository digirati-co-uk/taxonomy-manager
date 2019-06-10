package com.digirati.taxonomy.manager.lookup.persistence.model;

import org.apache.jena.rdf.model.Property;

import java.util.Objects;

public class ConceptSemanticRelationModel {

    private SemanticRelationType relation;

    private boolean transitive;

    private String sourceId;

    private String targetId;

    public SemanticRelationType getRelation() {
        return relation;
    }

    public ConceptSemanticRelationModel setRelation(SemanticRelationType relation) {
        this.relation = relation;
        return this;
    }

    public Property getRelationPredicate() {
        return relation.getProperty(transitive);
    }

    public boolean isTransitive() {
        return transitive;
    }

    public ConceptSemanticRelationModel setTransitive(boolean transitive) {
        this.transitive = transitive;
        return this;
    }

    public String getSourceId() {
        return sourceId;
    }

    public ConceptSemanticRelationModel setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    public String getTargetId() {
        return targetId;
    }

    public ConceptSemanticRelationModel setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }

    @Override
    public String toString() {
        return sourceId + " - " + relation.name() + "_" + transitive + " - " + targetId;
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
