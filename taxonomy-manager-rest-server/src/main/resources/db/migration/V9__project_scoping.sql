ALTER TABLE skos_concept_scheme ADD COLUMN project_id BIGINT REFERENCES project(id) NULL;
ALTER TABLE skos_concept ADD COLUMN project_id BIGINT REFERENCES project(id) NULL;

UPDATE skos_concept SET project_id = 1;
UPDATE skos_concept_scheme SET project_id = 1;

ALTER TABLE skos_concept ALTER COLUMN project_id SET NOT NULL;
ALTER TABLE skos_concept_scheme ALTER COLUMN project_id SET NOT NULL;
