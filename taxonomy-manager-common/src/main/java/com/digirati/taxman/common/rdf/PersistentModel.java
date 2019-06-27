package com.digirati.taxman.common.rdf;

import java.util.Optional;
import java.util.UUID;

public interface PersistentModel {
    Optional<UUID> getUuid();

    default boolean isNew() {
        return !getUuid().isPresent();
    }
}
