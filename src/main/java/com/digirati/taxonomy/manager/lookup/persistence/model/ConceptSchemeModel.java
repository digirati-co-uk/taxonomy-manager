package com.digirati.taxonomy.manager.lookup.persistence.model;

import java.util.Objects;

public class ConceptSchemeModel {

    // Primary Key, type bigserial
    private String id;

    // Type text
    private String iri;

    public String getId() {
        return id;
    }

    public ConceptSchemeModel setId(String id) {
        this.id = id;
        return this;
    }

    public String getIri() {
        return iri;
    }

    public ConceptSchemeModel setIri(String iri) {
        this.iri = iri;
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
        ConceptSchemeModel that = (ConceptSchemeModel) o;
        return Objects.equals(id, that.id) && Objects.equals(iri, that.iri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, iri);
    }
}
