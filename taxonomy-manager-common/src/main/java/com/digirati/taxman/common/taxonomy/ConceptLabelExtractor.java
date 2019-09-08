package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.RdfModelBuilder;
import com.digirati.taxman.common.taxonomy.Concept;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.SKOS;

import java.util.Map;
import java.util.function.BiConsumer;

public final class ConceptLabelExtractor {
    private final Concept concept;

    public ConceptLabelExtractor(Concept concept) {
        this.concept = concept;
    }

    public void extractTo(RdfModelBuilder<ConceptModel> builder) {
        extractTo(builder::addPlainLiteral);
    }

    public void extractTo(BiConsumer<Property, Map<String, String>> consumer) {
        consumer.accept(SKOS.prefLabel, concept.getPreferredLabel());
        consumer.accept(SKOS.altLabel, concept.getAltLabel());
        consumer.accept(SKOS.hiddenLabel, concept.getHiddenLabel());
        consumer.accept(SKOS.note, concept.getNote());
        consumer.accept(SKOS.changeNote, concept.getChangeNote());
        consumer.accept(SKOS.editorialNote, concept.getEditorialNote());
        consumer.accept(SKOS.example, concept.getExample());
        consumer.accept(SKOS.historyNote, concept.getHistoryNote());
        consumer.accept(SKOS.scopeNote, concept.getScopeNote());
    }
}
