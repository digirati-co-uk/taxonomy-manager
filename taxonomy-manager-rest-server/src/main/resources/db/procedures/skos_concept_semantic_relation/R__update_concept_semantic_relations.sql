DROP PROCEDURE IF EXISTS update_concept_semantic_relations;
create procedure update_concept_semantic_relations(_uuid uuid, _source character varying,
                                                   _relations skos_semantic_relation_data)
    language sql
as
$$
-- If there are any relations that we don't have database records for yet, create them.
INSERT INTO skos_concept (uuid, source)
SELECT (data ->> 'target_id')::uuid, (data ->> 'target_source')::varchar
FROM jsonb_array_elements(_relations) data
         LEFT JOIN skos_concept_ex sc ON sc.uuid = (data ->> 'target_id')::uuid
    OR sc.source = (data ->> 'target_source')
WHERE sc.uuid IS NULL
ON CONFLICT DO NOTHING;

-- Delete where relations do not exist in provided JSON data
DELETE
FROM skos_concept_semantic_relation scsr
    USING (
        SELECT r1.source_id, r1.target_id, r1.relation, r1.transitive
        FROM (SELECT c.id, c.uuid, c.source
              FROM skos_concept_ex c
              WHERE c.uuid = _uuid
                 OR c.source = _source
             ) src
                 LEFT JOIN skos_concept_semantic_relation r1
                           ON r1.source_id = src.id
                 LEFT JOIN (
            SELECT tc.id                                              target_id,
                   (data ->> 'relation')::skos_semantic_relation_type relation,
                   (data ->> 'transitive')::boolean                   transitive
            FROM jsonb_array_elements(_relations) data
                     LEFT JOIN skos_concept_ex tc
                               ON tc.uuid = (data ->> 'target_id')::uuid
                                   OR tc.source = (data ->> 'target_source')
            WHERE tc.id IS NOT NULL
        ) r2 ON (r1.target_id = r2.target_id AND r1.transitive = r2.transitive AND r1.relation = r2.relation)
        WHERE r2.target_id IS NULL
    ) relations
WHERE scsr.source_id = relations.source_id
  AND scsr.target_id = relations.target_id
  AND scsr.relation = relations.relation
  AND scsr.transitive = relations.transitive;


-- Now, add all new entries to the database
INSERT INTO skos_concept_semantic_relation(relation, transitive, source_id, target_id)
SELECT (data ->> 'relation')::skos_semantic_relation_type,
       (data ->> 'transitive')::boolean,
       sc.id,
       tc.id
FROM jsonb_array_elements(_relations) data
         LEFT JOIN skos_concept_ex sc
                   ON sc.uuid = _uuid OR sc.source = _source
         LEFT JOIN skos_concept_ex tc
                   ON tc.uuid = (data ->> 'target_id')::uuid
                       OR tc.source = (data ->> 'target_source')
ON CONFLICT DO NOTHING;
$$;
