package com.digirati.taxman.common.taxonomy;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Term {

    private final UUID uuid;

    private final Set<String> labels;

    public Term(UUID uuid, Set<String> labels) {
        this.uuid = uuid;
        this.labels = labels;
    }

    public Term(Concept concept, String languageKey) {
        this(concept.getUuid(), concept.getLabels(languageKey));
    }

    public UUID getUuid() {
        return uuid;
    }

    public Set<String> getLabels() {
        return labels;
    }
}
