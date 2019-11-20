package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptReference;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ConceptSchemeDataSet {
    private final ConceptSchemeRecord record;
    private final List<ConceptReference> topConcepts;
    private final String ownerSlug;

    public ConceptSchemeDataSet(ConceptSchemeRecord record, List<ConceptReference> topConcepts, String ownerSlug) {
        this.record = record;
        this.topConcepts = topConcepts;
        this.ownerSlug = ownerSlug;
    }

    public ConceptSchemeRecord getRecord() {
        return record;
    }

    public List<ConceptReference> getTopConcepts() {
        return topConcepts;
    }

    public JSONArray getTopConceptsJson() {
        var array = new JSONArray();

        for (var relationship : topConcepts) {
            var recordJson = new JSONObject();
            recordJson.put("uuid", relationship.getId());
            recordJson.put("source", relationship.getSource().orElse(null));
            array.put(recordJson);
        }

        return array;
    }

    public String getOwnerSlug() {
        return ownerSlug;
    }
}
