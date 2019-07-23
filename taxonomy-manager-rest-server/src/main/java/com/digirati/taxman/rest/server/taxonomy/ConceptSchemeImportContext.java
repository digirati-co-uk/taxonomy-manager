package com.digirati.taxman.rest.server.taxonomy;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.net.URI;
import java.util.UUID;

public class ConceptSchemeImportContext {
    /**
     * A [reverse] mapping of origin URIs to the internal UUIDs generated for the data records.
     */
    BiMap<URI, UUID> sources = HashBiMap.create();

    /**
     * A mapping of internal UUIDs to external model source URIs.
     */
    BiMap<UUID, URI> uuids = sources.inverse();

    public UUID store(URI uri) {
        UUID value = UUID.randomUUID();
        sources.put(uri, value);

        return value;
    }

    public ConceptSchemeImportContext() {

    }
}
