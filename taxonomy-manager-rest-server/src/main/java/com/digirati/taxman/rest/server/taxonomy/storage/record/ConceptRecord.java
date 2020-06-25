package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.digirati.taxman.common.taxonomy.Concept;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.UUID;

/**
 * A model of the `skos_concept` database in the taxman DDL.
 */
public class ConceptRecord implements Concept {
    private final UUID uuid;

    private String source;
    private String projectSlug;
    private Multimap<String, String> preferredLabel = ArrayListMultimap.create();
    private Multimap<String, String> altLabel = ArrayListMultimap.create();
    private Multimap<String, String> hiddenLabel = ArrayListMultimap.create();
    private Multimap<String, String> note = ArrayListMultimap.create();
    private Multimap<String, String> changeNote = ArrayListMultimap.create();
    private Multimap<String, String> editorialNote = ArrayListMultimap.create();
    private Multimap<String, String> example = ArrayListMultimap.create();
    private Multimap<String, String> historyNote = ArrayListMultimap.create();
    private Multimap<String, String> scopeNote = ArrayListMultimap.create();

    public ConceptRecord(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public Multimap<String, String> getPreferredLabel() {
        return preferredLabel;
    }

    public void setPreferredLabel(Multimap<String, String> preferredLabel) {
        this.preferredLabel = preferredLabel;
    }

    @Override
    public Multimap<String, String> getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(Multimap<String, String> altLabel) {
        this.altLabel = altLabel;
    }

    @Override
    public Multimap<String, String> getHiddenLabel() {
        return hiddenLabel;
    }

    public void setHiddenLabel(Multimap<String, String> hiddenLabel) {
        this.hiddenLabel = hiddenLabel;
    }

    @Override
    public Multimap<String, String> getNote() {
        return note;
    }

    public void setNote(Multimap<String, String> note) {
        this.note = note;
    }

    @Override
    public Multimap<String, String> getChangeNote() {
        return changeNote;
    }

    public void setChangeNote(Multimap<String, String> changeNote) {
        this.changeNote = changeNote;
    }

    @Override
    public Multimap<String, String> getEditorialNote() {
        return editorialNote;
    }

    public void setEditorialNote(Multimap<String, String> editorialNote) {
        this.editorialNote = editorialNote;
    }

    @Override
    public Multimap<String, String> getExample() {
        return example;
    }

    public void setExample(Multimap<String, String> example) {
        this.example = example;
    }

    @Override
    public Multimap<String, String> getHistoryNote() {
        return historyNote;
    }

    public void setHistoryNote(Multimap<String, String> historyNote) {
        this.historyNote = historyNote;
    }

    @Override
    public Multimap<String, String> getScopeNote() {
        return scopeNote;
    }

    public void setScopeNote(Multimap<String, String> scopeNote) {
        this.scopeNote = scopeNote;
    }

    public String getProjectSlug() {
        return projectSlug;
    }

    public void setProjectSlug(String projectSlug) {
        this.projectSlug = projectSlug;
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
                .add("source", source)
                .add("projectSlug", projectSlug)
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
