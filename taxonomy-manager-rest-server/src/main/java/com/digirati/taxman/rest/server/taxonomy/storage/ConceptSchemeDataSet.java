package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptSchemeRecord;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ConceptSchemeDataSet {
    private final ConceptSchemeRecord record;
    private final List<ConceptRecord> topConcepts;

    public ConceptSchemeDataSet(ConceptSchemeRecord record, List<ConceptRecord> topConcepts) {
        this.record = record;
        this.topConcepts = topConcepts;
    }

    public ConceptSchemeRecord getRecord() {
        return record;
    }

    public List<ConceptRecord> getTopConcepts() {
        return topConcepts;
    }

    public JSONArray getTopConceptsJson() {
        var array = new JSONArray();

        for (var relationship : topConcepts) {
            var recordJson = new JSONObject();
            recordJson.put("uuid", relationship.getUuid());
            recordJson.put("source", relationship.getSource());
            array.put(recordJson);
        }

        return array;
    }
}
