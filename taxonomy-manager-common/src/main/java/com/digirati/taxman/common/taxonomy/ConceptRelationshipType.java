package com.digirati.taxman.common.taxonomy;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.SKOS;

public enum ConceptRelationshipType {
    BROADER(SKOS.broader, SKOS.broaderTransitive),
    NARROWER(SKOS.narrower, SKOS.narrowerTransitive),
    RELATED(SKOS.related);

    public static final ConceptRelationshipType[] VALUES = values();
    private final Property relationshipProperty;
    private final Property transitiveRelationshipProperty;

    ConceptRelationshipType(Property relationshipProperty, Property transitiveRelationshipProperty) {
        this.relationshipProperty = relationshipProperty;
        this.transitiveRelationshipProperty = transitiveRelationshipProperty;
    }

    ConceptRelationshipType(Property relationshipProperty) {
        this(relationshipProperty, null);
    }

    /**
     * Check if this relationship type supports a transitive variant.
     *
     * @return {@code true} iff a transitive variant is supported.
     */
    public boolean hasTransitiveProperty() {
        return transitiveRelationshipProperty != null;
    }

    /**
     * Get the RDF {@code Property} that this relationship represents.
     *
     * @param transitive If the transitive variant of the property should be returned.
     * @return An RDF property representing the relationship.
     */
    public Property getSkosProperty(boolean transitive) {
        if (transitive && transitiveRelationshipProperty == null) {
            throw new IllegalArgumentException("Relationship type does not support transitive properties");
        }

        return transitive ? transitiveRelationshipProperty : relationshipProperty;
    }
}
