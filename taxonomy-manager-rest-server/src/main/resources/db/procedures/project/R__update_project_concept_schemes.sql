DROP PROCEDURE IF EXISTS update_project_concept_schemes(character varying, uuid[]);
CREATE OR REPLACE PROCEDURE update_project_concept_schemes(_slug character varying, _scheme_uuids uuid[])
LANGUAGE sql AS
$$
-- First, add all new entries to the database, ignoring items that are already there.
INSERT INTO project_skos_concept_scheme (project_id, concept_scheme_id)
SELECT p.id, cs.id
FROM unnest(_scheme_uuids) scheme_uuid
    INNER JOIN skos_concept_scheme cs
        ON cs.uuid = scheme_uuid
    INNER JOIN project p
        ON p.slug = _slug
ON CONFLICT DO NOTHING;

-- Then, delete any existing entries that were not in the list provided.
DELETE FROM project_skos_concept_scheme USING project_skos_concept_scheme pcs
    INNER JOIN skos_concept_scheme cs
        ON cs.id = pcs.concept_scheme_id
    INNER JOIN project p
        ON p.id = pcs.project_id
    LEFT OUTER JOIN unnest(_scheme_uuids) scheme_uuid
        ON scheme_uuid = cs.uuid
WHERE project_skos_concept_scheme.concept_scheme_id = pcs.concept_scheme_id
  AND project_skos_concept_scheme.project_id = p.id
  AND scheme_uuid IS NULL
  AND p.slug = _slug;
$$;
