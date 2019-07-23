package com.digirati.taxman.rest.server.taxonomy;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.net.URI;
import java.util.UUID;

public class ModelMappingContext {

    private final BiMap<URI, UUID> uuids = HashBiMap.create();
    private final BiMap<UUID, URI> uris = uuids.inverse();

    public void addMapping(URI uri, UUID uuid) {
        uuids.put(uri, uuid);
    }

    public void deleteMapping(URI uri) {
        uuids.remove(uri);
    }

    public UUID getMapping(URI uri) {
        return uuids.get(uri);
    }
}
