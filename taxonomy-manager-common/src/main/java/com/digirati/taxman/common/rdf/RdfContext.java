package com.digirati.taxman.common.rdf;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.net.URI;
import java.util.UUID;

public class RdfContext {
    RdfModelFactory modelFactory;
    BiMap<URI, UUID> uuids = HashBiMap.create();
    BiMap<UUID, URI> uris = uuids.inverse();
}
