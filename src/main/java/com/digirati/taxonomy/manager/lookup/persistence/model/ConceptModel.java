package com.digirati.taxonomy.manager.lookup.persistence.model;

import java.util.Objects;

public class ConceptModel {

    // Primary key - of type bigint
    private Long id;

    // Type text
    private String iri;

    // Type jsonb
    private String preferredLabel;
    private String altLabel;
    private String hiddenLabel;
    private String note;
    private String changeNote;
    private String editorialNote;
    private String example;
    private String historyNote;
    private String scopeNote;

    public Long getId() {
        return id;
    }

    public ConceptModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getIri() {
        return iri;
    }

    public ConceptModel setIri(String iri) {
        this.iri = iri;
        return this;
    }

    public String getPreferredLabel() {
        return preferredLabel;
    }

    public ConceptModel setPreferredLabel(String preferredLabel) {
        this.preferredLabel = preferredLabel;
        return this;
    }

    public String getAltLabel() {
        return altLabel;
    }

    public ConceptModel setAltLabel(String altLabel) {
        this.altLabel = altLabel;
        return this;
    }

    public String getHiddenLabel() {
        return hiddenLabel;
    }

    public ConceptModel setHiddenLabel(String hiddenLabel) {
        this.hiddenLabel = hiddenLabel;
        return this;
    }

    public String getNote() {
        return note;
    }

    public ConceptModel setNote(String note) {
        this.note = note;
        return this;
    }

    public String getChangeNote() {
        return changeNote;
    }

    public ConceptModel setChangeNote(String changeNote) {
        this.changeNote = changeNote;
        return this;
    }

    public String getEditorialNote() {
        return editorialNote;
    }

    public ConceptModel setEditorialNote(String editorialNote) {
        this.editorialNote = editorialNote;
        return this;
    }

    public String getExample() {
        return example;
    }

    public ConceptModel setExample(String example) {
        this.example = example;
        return this;
    }

    public String getHistoryNote() {
        return historyNote;
    }

    public ConceptModel setHistoryNote(String historyNote) {
        this.historyNote = historyNote;
        return this;
    }

    public String getScopeNote() {
        return scopeNote;
    }

    public ConceptModel setScopeNote(String scopeNote) {
        this.scopeNote = scopeNote;
        return this;
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
                && Objects.equals(iri, that.iri)
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
                iri,
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
