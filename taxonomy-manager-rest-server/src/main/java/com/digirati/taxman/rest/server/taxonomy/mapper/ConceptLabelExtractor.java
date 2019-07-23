package com.digirati.taxman.rest.server.taxonomy.mapper;

import com.digirati.taxman.common.rdf.RdfModelBuilder;
import com.digirati.taxman.common.taxonomy.Concept;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import org.apache.jena.vocabulary.SKOS;

public final class ConceptLabelExtractor {
    private final Concept concept;

    public ConceptLabelExtractor(Concept concept) {
        this.concept = concept;
    }

    public void extractTo(RdfModelBuilder<ConceptModel> builder) {
        builder.addPlainLiteral(SKOS.prefLabel, concept.getPreferredLabel())
                .addPlainLiteral(SKOS.altLabel, concept.getAltLabel())
                .addPlainLiteral(SKOS.hiddenLabel, concept.getHiddenLabel())
                .addPlainLiteral(SKOS.note, concept.getNote())
                .addPlainLiteral(SKOS.changeNote, concept.getChangeNote())
                .addPlainLiteral(SKOS.editorialNote, concept.getEditorialNote())
                .addPlainLiteral(SKOS.example, concept.getExample())
                .addPlainLiteral(SKOS.historyNote, concept.getHistoryNote())
                .addPlainLiteral(SKOS.scopeNote, concept.getScopeNote());
    }
}
