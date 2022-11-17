package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.rdf.PersistentModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.rest.server.infrastructure.config.RdfConfig;
import org.apache.jena.rdf.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * An extra bank of N-triples that can be associated with a {@link org.apache.jena.rdf.model.Resource} to add
 * non-SKOS properties.
 */
public class ExtraTripleBank {
    public static final Model m = ModelFactory.createDefaultModel();
    public static final Map<String, Map<Property, Statement>> CRU_STMTS = new ConcurrentHashMap<>();

    public static void createInitialTriple(Property group, Property inverse, Map<String, String> collection) {
        for (var entry : collection.entrySet()) {
            var conceptUuid = "http://backend.dev.digirati.taxman.digirati.io/v0.1/concept/" + entry.getKey();
            var groupUuid = "http://backend.dev.digirati.taxman.digirati.io/v0.1/concept/" + entry.getValue();
            var resource = m.createResource(conceptUuid);
            var groupResource = m.createResource(groupUuid);
            var stmt = m.createStatement(resource, group, groupResource);

            addOverride(conceptUuid, group, stmt);
        }

        for (var value : collection.values()) {
            var groupUuid = "http://backend.dev.digirati.taxman.digirati.io/v0.1/concept/" + value;
            var groupResource = m.createResource(groupUuid);
            var inverseStmt = m.createLiteralStatement(groupResource, inverse, true);

            addOverride(groupUuid, inverse, inverseStmt);
        }
    }

    static {
        createInitialTriple(RdfConfig.inRegionGroup, RdfConfig.isRegionGroup, RdfConfig.CONCEPT_TO_REGION_GROUP);
        createInitialTriple(RdfConfig.inTopicGroup, RdfConfig.isTopicGroup, RdfConfig.CONCEPT_TO_TOPIC_GROUP);
        createInitialTriple(RdfConfig.inCommodityGroup, RdfConfig.isCommodityGroup, RdfConfig.CONCEPT_TO_COMMODITY_GROUP);
    }

    public static void addOverride(String uuid, Property property, Statement statement) {
        var map = CRU_STMTS.computeIfAbsent(uuid, (k) -> new HashMap<>());
        map.put(property, statement);
    }

    public static Stream<Statement> getStatementsFor(Resource resource) {
        var key = resource.getURI();

        return CRU_STMTS.getOrDefault(key, new HashMap<>())
                .values()
                .stream();
    }

    public static void storeStatementsFrom(Resource resource) {
        var statements = resource.listProperties();
        var newStatements = new HashMap<Property, Statement>();

        while (statements.hasNext()) {
            var statement = statements.next();
            var property = statement.getPredicate();

            if (!property.getNameSpace().equals(RdfConfig.uri)) {
                continue;
            }

            newStatements.put(property, statement);
        }

        CRU_STMTS.put(resource.getURI(), newStatements);
    }
}
