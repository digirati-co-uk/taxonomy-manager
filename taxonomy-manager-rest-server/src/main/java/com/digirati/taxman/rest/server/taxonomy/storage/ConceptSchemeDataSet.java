package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;

import java.util.List;

public class ConceptSchemeDataSet {
    private final ConceptSchemeRecord record;
    private final List<ConceptReference> topConcepts;

    public ConceptSchemeDataSet(ConceptSchemeRecord record, List<ConceptReference> topConcepts) {
        this.record = record;
        this.topConcepts = topConcepts;
    }

    public ConceptSchemeRecord getRecord() {
        return record;
    }

    public List<ConceptReference> getTopConcepts() {
        return topConcepts;
    }
}
