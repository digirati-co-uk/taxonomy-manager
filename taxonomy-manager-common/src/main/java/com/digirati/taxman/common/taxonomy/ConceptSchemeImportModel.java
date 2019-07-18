package com.digirati.taxman.common.taxonomy;

import java.util.Collection;

public class ConceptSchemeImportModel {

    private final ConceptSchemeModel conceptScheme;

    private final Collection<ConceptModel> concepts;

    public ConceptSchemeImportModel(ConceptSchemeModel conceptScheme, Collection<ConceptModel> concepts) {
        this.conceptScheme = conceptScheme;
        this.concepts = concepts;
    }

    public ConceptSchemeModel getConceptScheme() {
        return conceptScheme;
    }

    public Collection<ConceptModel> getConcepts() {
        return concepts;
    }
}
