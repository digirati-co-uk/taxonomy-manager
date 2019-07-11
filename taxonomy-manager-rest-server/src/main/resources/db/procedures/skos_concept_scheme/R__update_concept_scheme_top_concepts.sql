DROP PROCEDURE IF EXISTS update_concept_scheme_top_concepts;
CREATE OR REPLACE PROCEDURE update_concept_scheme_top_concepts(_uuid uuid,
                                                               _concept_uuids uuid[])
    LANGUAGE SQL AS
$$

-- First, add all new entries to the database, ignoring items that are already there.

INSERT INTO skos_concept_scheme_concept (concept_id, concept_scheme_id, is_top_concept)
SELECT c.id,
    cs.id,
    true
FROM unnest(_concept_uuids) concept_uuid
    INNER JOIN skos_concept c
        ON c.uuid = concept_uuid
    INNER JOIN skos_concept_scheme cs
        ON cs.uuid = _uuid
ON CONFLICT DO NOTHING;

-- Then, delete any existing entries that were not in the list provided.
DELETE FROM skos_concept_scheme_concept USING skos_concept_scheme_concept csc
    INNER JOIN skos_concept c
        ON c.id = csc.concept_id

    INNER JOIN skos_concept_scheme cs
        ON cs.id = csc.concept_scheme_id

    LEFT OUTER JOIN unnest(_concept_uuids) concept_uuid
        ON concept_uuid = c.uuid
WHERE skos_concept_scheme_concept.concept_scheme_id = csc.concept_scheme_id
  AND skos_concept_scheme_concept.concept_id = csc.concept_id
  AND skos_concept_scheme_concept.is_top_concept = true
  AND concept_uuid IS NULL;

$$;
