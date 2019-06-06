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

    private final Property nonTransitive;

    private final Property transitive;

    SemanticRelationType(Property nonTransitive, Property transitive) {
        this.nonTransitive = nonTransitive;
        this.transitive = transitive;
    }

    public Property getProperty(boolean isTransitive) {
        if (isTransitive) {
            return transitive;
        }
        return nonTransitive;
    }
}
