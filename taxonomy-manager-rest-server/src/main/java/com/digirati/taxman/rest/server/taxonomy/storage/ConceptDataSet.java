package com.digirati.taxman.rest.server.taxonomy.storage;

import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRecord;
import com.digirati.taxman.rest.server.taxonomy.storage.record.ConceptRelationshipRecord;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A dataset containing all the information stored in the underlying persistence backend
 * about a concept.
 */
public class ConceptDataSet {
    private final ConceptRecord record;
    private final List<ConceptRelationshipRecord> relationships;

    public ConceptDataSet(ConceptRecord record) {
        this(record, new ArrayList<>());
    }

    public ConceptDataSet(ConceptRecord record, List<ConceptRelationshipRecord> relationshipRecords) {
        this.record = record;
        this.relationships = relationshipRecords;
    }

    public void addRelationshipRecord(ConceptRelationshipRecord record) {
        relationships.add(record);
    }

    /**
     * Get the database records for the relationships this concept has.
     *
     * @return the relationship database records for this concept.
     */
    public List<ConceptRelationshipRecord> getRelationshipRecords() {
        return List.copyOf(relationships);
    }

    /**
     * Get the underlying record for the concept row this {@link com.digirati.taxman.rest.server.taxonomy.storage.ConceptDataSet} models.
     *
     * @return the database record for this concept.
     */
    public ConceptRecord getRecord() {
        return record;
    }

    /**
     * Serialize the relationships contained within this dataset to a form accepted
     * by the <code>update_concept_relationships(uuid, jsonb)</code> procedure.
     *
     * @return A JSON object containing relationship data that can be passed directly to the persistence layer.
     */
    JSONArray getRelationshipRecordsJson() {
        var array = new JSONArray();

        for (var record : relationships) {
            var recordJson = new JSONObject();
            recordJson.put("source_id", record.getSource().toString());
            recordJson.put("target_id", record.getTarget().toString());
            recordJson.put("relation", record.getType().toString().toLowerCase());
            recordJson.put("transitive", record.isTransitive());

            array.put(recordJson);
        }

        return array;
    }
}
