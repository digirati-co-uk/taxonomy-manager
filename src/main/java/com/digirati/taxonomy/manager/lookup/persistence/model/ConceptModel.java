package com.digirati.taxonomy.manager.lookup.persistence.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/** Models a SKOS concept. */
public class ConceptModel {

    private final String id;
    private final JsonNode preferredLabel;
    private final JsonNode altLabel;
    private final JsonNode hiddenLabel;
    private final JsonNode note;
    private final JsonNode changeNote;
    private final JsonNode editorialNote;
    private final JsonNode example;
    private final JsonNode historyNote;
    private final JsonNode scopeNote;

    public ConceptModel(
            String id,
            JsonNode preferredLabel,
            JsonNode altLabel,
            JsonNode hiddenLabel,
            JsonNode note,
            JsonNode changeNote,
            JsonNode editorialNote,
            JsonNode example,
            JsonNode historyNote,
            JsonNode scopeNote) {
        this.id = id;
        this.preferredLabel = preferredLabel;
        this.altLabel = altLabel;
        this.hiddenLabel = hiddenLabel;
        this.note = note;
        this.changeNote = changeNote;
        this.editorialNote = editorialNote;
        this.example = example;
        this.historyNote = historyNote;
        this.scopeNote = scopeNote;
    }

    public String getId() {
        return id;
    }

    public JsonNode getPreferredLabel() {
        return preferredLabel;
    }

    public JsonNode getAltLabel() {
        return altLabel;
    }

    public JsonNode getHiddenLabel() {
        return hiddenLabel;
    }

    public JsonNode getNote() {
        return note;
    }

    public JsonNode getChangeNote() {
        return changeNote;
    }

    public JsonNode getEditorialNote() {
        return editorialNote;
    }

    public JsonNode getExample() {
        return example;
    }

    public JsonNode getHistoryNote() {
        return historyNote;
    }

    public JsonNode getScopeNote() {
        return scopeNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConceptModel that = (ConceptModel) o;
        return Objects.equals(id, that.id)
                && Objects.equals(preferredLabel, that.preferredLabel)
                && Objects.equals(altLabel, that.altLabel)
                && Objects.equals(hiddenLabel, that.hiddenLabel)
                && Objects.equals(note, that.note)
                && Objects.equals(changeNote, that.changeNote)
                && Objects.equals(editorialNote, that.editorialNote)
                && Objects.equals(example, that.example)
                && Objects.equals(historyNote, that.historyNote)
                && Objects.equals(scopeNote, that.scopeNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                preferredLabel,
                altLabel,
                hiddenLabel,
                note,
                changeNote,
                editorialNote,
                example,
                historyNote,
                scopeNote);
    }
}
