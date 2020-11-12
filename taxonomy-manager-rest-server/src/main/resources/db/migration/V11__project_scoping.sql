INSERT INTO project (slug, title) VALUES ('orphaned-project', '{"en": "Default project for ophaned resources"}');

ALTER TABLE skos_concept_scheme
    ADD COLUMN project_id bigint null;

UPDATE skos_concept_scheme AS cs
SET project_id = pscs.project_id
FROM project_skos_concept_scheme pscs
WHERE pscs.concept_scheme_id = cs.id;

UPDATE skos_concept_scheme AS cs
SET project_id = (SELECT id from project WHERE slug = 'orphaned-project')
WHERE cs.project_id IS NULL;

ALTER TABLE skos_concept ADD COLUMN project_id BIGINT NULL;

UPDATE skos_concept AS c
SET project_id = cs.project_id
FROM skos_concept_scheme cs,
     skos_concept_scheme_concept cs_c
WHERE cs_c.concept_id = c.id
  AND cs.id = cs_c.concept_scheme_id;

UPDATE skos_concept AS c
SET project_id = (SELECT id from project WHERE slug = 'orphaned-project')
WHERE c.project_id IS NULL;

ALTER TABLE skos_concept ALTER COLUMN project_id SET NOT NULL;

DROP TABLE project_skos_concept_scheme;

CREATE VIEW project_skos_concept_scheme AS
SELECT cs.project_id, cs.id AS concept_scheme_id
FROM skos_concept_scheme cs;
