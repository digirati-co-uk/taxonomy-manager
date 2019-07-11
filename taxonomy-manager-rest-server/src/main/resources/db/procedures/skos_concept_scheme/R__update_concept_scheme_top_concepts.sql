DROP PROCEDURE IF EXISTS update_concept_scheme_top_concepts(uuid, uuid[]);
CREATE OR REPLACE PROCEDURE update_concept_scheme_top_concepts(_uuid uuid,
                                                               _concept_uuids uuid[])
    LANGUAGE SQL AS
$$
-- First, delete all records associated with this scheme
DELETE FROM skos_concept_scheme_concept USING skos_concept_scheme_concept csc
    INNER JOIN skos_concept_scheme cs ON cs.id = csc.concept_scheme_id
WHERE cs.uuid = _uuid;

-- Then, add all new entries to the database
INSERT INTO skos_concept_scheme_concept (concept_id, concept_scheme_id, is_top_concept)
SELECT c.id,
    cs.id,
    true
FROM unnest(_concept_uuids) concept_uuid
    INNER JOIN skos_concept c
        ON c.uuid = concept_uuid
    INNER JOIN skos_concept_scheme cs
        ON cs.uuid = _uuid;
$$;
