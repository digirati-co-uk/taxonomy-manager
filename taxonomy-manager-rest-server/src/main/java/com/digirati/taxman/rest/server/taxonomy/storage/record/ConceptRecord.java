package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A model of the `skos_concept` database in the taxman DDL.
 */
public class ConceptRecord {
    private final UUID uuid;

    private Map<String, String> preferredLabel = new HashMap<>();
    private Map<String, String> altLabel = new HashMap<>();
    private Map<String, String> hiddenLabel = new HashMap<>();
    private Map<String, String> note = new HashMap<>();
    private Map<String, String> changeNote = new HashMap<>();
    private Map<String, String> editorialNote = new HashMap<>();
    private Map<String, String> example = new HashMap<>();
    private Map<String, String> historyNote = new HashMap<>();
    private Map<String, String> scopeNote = new HashMap<>();

    public ConceptRecord(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<String, String> getPreferredLabel() {
        return preferredLabel;
    }

    public void setPreferredLabel(Map<String, String> preferredLabel) {
        this.preferredLabel = preferredLabel;
    }

    public Map<String, String> getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(Map<String, String> altLabel) {
        this.altLabel = altLabel;
    }

    public Map<String, String> getHiddenLabel() {
        return hiddenLabel;
    }

    public void setHiddenLabel(Map<String, String> hiddenLabel) {
        this.hiddenLabel = hiddenLabel;
    }

    public Map<String, String> getNote() {
        return note;
    }

    public void setNote(Map<String, String> note) {
        this.note = note;
    }

    public Map<String, String> getChangeNote() {
        return changeNote;
    }

    public void setChangeNote(Map<String, String> changeNote) {
        this.changeNote = changeNote;
    }

    public Map<String, String> getEditorialNote() {
        return editorialNote;
    }

    public void setEditorialNote(Map<String, String> editorialNote) {
        this.editorialNote = editorialNote;
    }

    public Map<String, String> getExample() {
        return example;
    }

    public void setExample(Map<String, String> example) {
        this.example = example;
    }

    public Map<String, String> getHistoryNote() {
        return historyNote;
    }

    public void setHistoryNote(Map<String, String> historyNote) {
        this.historyNote = historyNote;
    }

    public Map<String, String> getScopeNote() {
        return scopeNote;
    }

    public void setScopeNote(Map<String, String> scopeNote) {
        this.scopeNote = scopeNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptRecord that = (ConceptRecord) o;
        return Objects.equal(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("preferredLabel", preferredLabel)
                .add("altLabel", altLabel)
                .add("hiddenLabel", hiddenLabel)
                .add("note", note)
                .add("changeNote", changeNote)
                .add("editorialNote", editorialNote)
                .add("example", example)
                .add("historyNote", historyNote)
                .add("scopeNote", scopeNote)
                .toString();
    }
}
