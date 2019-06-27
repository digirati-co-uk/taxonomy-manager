DROP DOMAIN IF EXISTS skos_semantic_relation_data;
CREATE DOMAIN skos_semantic_relation_data AS JSONB;

DROP PROCEDURE IF EXISTS update_concept_semantic_relations;
CREATE OR REPLACE PROCEDURE update_concept_semantic_relations(
    _uuid uuid,
    _relations skos_semantic_relation_data) LANGUAGE SQL AS
$$
    -- First, add all new entries to the database, ignoring items that are already there.
    INSERT INTO skos_concept_semantic_relation(relation, transitive, source_id, target_id)
    SELECT (data ->> 'relation')::skos_semantic_relation_type,
        (data ->> 'transitive')::boolean,
        sc.id,
        tc.id
    FROM jsonb_array_elements(_relations) data
        INNER JOIN skos_concept sc
            ON sc.uuid = (data ->> 'source_id')::uuid
        INNER JOIN skos_concept tc
            ON tc.uuid = (data ->> 'target_id')::uuid
    ON CONFLICT DO NOTHING;

    -- Then, delete any existing entries that were not in the list provided.
    DELETE FROM skos_concept_semantic_relation
    USING skos_concept_semantic_relation relation
        INNER JOIN skos_concept sc
            ON sc.id = relation.source_id AND sc.uuid = _uuid
        INNER JOIN skos_concept tc
            ON tc.id = relation.target_id
        LEFT OUTER JOIN jsonb_array_elements(_relations) AS data
            ON (data ->> 'source_id')::uuid = sc.uuid AND
               (data ->> 'target_id')::uuid = tc.uuid AND
               (data ->> 'relation')::skos_semantic_relation_type = relation.relation AND
               (data ->> 'transitive')::boolean = relation.transitive
    WHERE skos_concept_semantic_relation.source_id = relation.source_id
      AND skos_concept_semantic_relation.target_id = relation.target_id
      AND skos_concept_semantic_relation.relation = relation.relation
      AND skos_concept_semantic_relation.transitive = relation.transitive
      AND (data->>'source_id') IS NULL;
$$;
