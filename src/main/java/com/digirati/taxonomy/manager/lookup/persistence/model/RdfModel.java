package com.digirati.taxonomy.manager.lookup.persistence.model;

import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RdfModel rdfModel = (RdfModel) o;
        return Objects.equals(concepts, rdfModel.concepts)
                && Objects.equals(conceptSchemes, rdfModel.conceptSchemes)
                && Objects.equals(relationships, rdfModel.relationships);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concepts, conceptSchemes, relationships);
    }
}
