DROP PROCEDURE IF EXISTS update_concept_semantic_relations;
create procedure update_concept_semantic_relations(_uuid uuid, _source varchar, _relations skos_semantic_relation_data)
    language sql
as
$$
    -- If there are any relations that we don't have database records for yet, create them.
INSERT INTO skos_concept (uuid, source)
SELECT (data ->> 'target_id')::uuid, (data ->> 'target_source')::varchar
FROM jsonb_array_elements(_relations) data
         LEFT JOIN skos_concept sc ON sc.uuid = (data ->> 'target_id')::uuid
    OR sc.source = (data ->> 'target_source')
WHERE sc.uuid IS NULL
ON CONFLICT DO NOTHING;

-- First, add all new entries to the database, ignoring items that are already there.
INSERT INTO skos_concept_semantic_relation(relation, transitive, source_id, target_id)
SELECT (data ->> 'relation')::skos_semantic_relation_type,
       (data ->> 'transitive')::boolean,
       sc.id,
       tc.id
FROM jsonb_array_elements(_relations) data
         LEFT JOIN skos_concept sc
                    ON sc.uuid = _uuid OR sc.source = _source
         LEFT JOIN skos_concept tc
                    ON tc.uuid = (data ->> 'target_id')::uuid
                        OR tc.source = (data ->> 'target_source')
ON CONFLICT DO NOTHING;
$$;
