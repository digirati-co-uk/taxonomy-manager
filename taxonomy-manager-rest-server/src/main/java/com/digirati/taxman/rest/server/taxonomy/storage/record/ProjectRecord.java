package com.digirati.taxman.rest.server.taxonomy.storage.record;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;

public class ProjectRecord {

    private final String slug;

    private Map<String, String> title = new HashMap<>();

    public ProjectRecord(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectRecord that = (ProjectRecord) o;
        return Objects.equal(slug, that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(slug);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("slug", slug)
                .add("title", title)
                .toString();
    }
}
