package com.digirati.taxonomy.manager.lookup.persistence.model;

import java.util.Collection;

/** Models the contents of a SKOS file. */
public class RdfModel {

    private final Collection<ConceptModel> concepts;

    private final Collection<ConceptSchemeModel> conceptSchemes;

    private final Collection<ConceptSemanticRelationModel> relationships;

    public RdfModel(
            Collection<ConceptModel> concepts,
            Collection<ConceptSchemeModel> conceptSchemes,
            Collection<ConceptSemanticRelationModel> relationships) {
        this.concepts = concepts;
        this.conceptSchemes = conceptSchemes;
        this.relationships = relationships;
    }

    public Collection<ConceptModel> getConcepts() {
        return concepts;
    }

    public Collection<ConceptSchemeModel> getConceptSchemes() {
        return conceptSchemes;
    }

    public Collection<ConceptSemanticRelationModel> getRelationships() {
        return relationships;
    }
}
