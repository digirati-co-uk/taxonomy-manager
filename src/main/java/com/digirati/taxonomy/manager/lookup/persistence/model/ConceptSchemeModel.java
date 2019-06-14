package com.digirati.taxonomy.manager.lookup.persistence.model;

import java.util.Objects;

/** Models a SKOS concept scheme. */
public class ConceptSchemeModel {

    private final String id;

    private final String title;

    public ConceptSchemeModel(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
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
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
