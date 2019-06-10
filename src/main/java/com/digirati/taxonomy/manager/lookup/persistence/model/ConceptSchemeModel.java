package com.digirati.taxonomy.manager.lookup.persistence.model;

import java.util.Objects;

// TODO add text field
public class ConceptSchemeModel {

    private String id;

    public String getId() {
        return id;
    }

    public ConceptSchemeModel setId(String id) {
        this.id = id;
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
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
