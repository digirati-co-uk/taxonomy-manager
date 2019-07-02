package com.digirati.taxman.common.rdf;

import java.util.UUID;

public interface PersistentModel {
    UUID getUuid();

    default boolean isNew() {
        return getUuid() != null;
    }
}
