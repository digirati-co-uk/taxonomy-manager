package com.digirati.taxonomy.manager.lookup.persistence.model;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.SKOS;

public enum SemanticRelationType {
    BROADER(SKOS.broader, SKOS.broaderTransitive),
    NARROWER(SKOS.narrower, SKOS.narrowerTransitive),
    RELATED(SKOS.related, SKOS.related),
    IN_SCHEME(SKOS.inScheme, SKOS.inScheme),
    HAS_TOP_CONCEPT(SKOS.hasTopConcept, SKOS.hasTopConcept),
    TOP_CONCEPT_OF(SKOS.topConceptOf, SKOS.topConceptOf);

    private final Property nonTransitiveRdfProperty;

    private final Property transitiveRdfProperty;

    SemanticRelationType(Property nonTransitiveRdfProperty, Property transitiveRdfProperty) {
        this.nonTransitiveRdfProperty = nonTransitiveRdfProperty;
        this.transitiveRdfProperty = transitiveRdfProperty;
    }

    public Property getProperty(boolean isTransitive) {
        return isTransitive ? transitiveRdfProperty : nonTransitiveRdfProperty;
    }

    public static boolean isMappableRdfProperty(Property rdfProperty) {
        for (SemanticRelationType relationType : values()) {
            if (relationType.transitiveRdfProperty.equals(rdfProperty)
                    || relationType.nonTransitiveRdfProperty.equals(rdfProperty)) {
                return true;
            }
        }
        return false;
    }

    public static RelationshipGenerator getRelationshipGenerator(Property rdfProperty) {
        for (SemanticRelationType relationType : values()) {
            if (relationType.nonTransitiveRdfProperty.equals(rdfProperty)) {
                return (sourceId, targetId) ->
                        new ConceptSemanticRelationModel(sourceId, targetId, relationType, false);
            } else if (relationType.transitiveRdfProperty.equals(rdfProperty)) {
                return (sourceId, targetId) ->
                        new ConceptSemanticRelationModel(sourceId, targetId, relationType, true);
            }
        }
        throw new IllegalArgumentException(
                "Unable to create relationship generator for: " + rdfProperty);
    }

    @FunctionalInterface
    public interface RelationshipGenerator {
        ConceptSemanticRelationModel generate(String sourceId, String targetId);
    }
}
