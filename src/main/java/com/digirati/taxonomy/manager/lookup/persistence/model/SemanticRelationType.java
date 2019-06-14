package com.digirati.taxonomy.manager.lookup.persistence.model;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.SKOS;

/**
 * Enum of all possible relationships between SKOS entities that we are interested in processing.
 */
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

    /**
     * Gets the RDF property represented by this relation type.
     *
     * @param isTransitive boolean for whether to get the transitive or non-transitive form of the
     *     RDF property.
     * @return the RDF property corresponding to this relation type.
     */
    public Property getProperty(boolean isTransitive) {
        return isTransitive ? transitiveRdfProperty : nonTransitiveRdfProperty;
    }

    /**
     * Determines if a given RDF property corresponds to one of the supported relation types.
     *
     * @param rdfProperty the property to check for
     * @return true if the input property corresponds to a supported relation type; false otherwise.
     */
    public static boolean isMappableRdfProperty(Property rdfProperty) {
        for (SemanticRelationType relationType : values()) {
            if (relationType.transitiveRdfProperty.equals(rdfProperty)
                    || relationType.nonTransitiveRdfProperty.equals(rdfProperty)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a {@link RelationshipGenerator} from which a {@link ConceptSemanticRelationModel}
     * corresponding to the input RDF property can be retrieved.
     *
     * @param rdfProperty the RDF property for which to generate the relationship.
     * @return a {@link RelationshipGenerator} capable of generating the desired relationship.
     * @throws IllegalArgumentException if the input RDF property does not correspond to a relation
     *     type.
     */
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

    /**
     * Functional interface to generate a {@link ConceptSemanticRelationModel} given the IDs of the
     * related entities.
     */
    @FunctionalInterface
    public interface RelationshipGenerator {
        ConceptSemanticRelationModel generate(String sourceId, String targetId);
    }
}
