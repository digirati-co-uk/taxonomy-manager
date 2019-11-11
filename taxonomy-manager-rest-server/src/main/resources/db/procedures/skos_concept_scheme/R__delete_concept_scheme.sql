DROP PROCEDURE IF EXISTS delete_concept_scheme;
CREATE OR REPLACE PROCEDURE delete_concept_scheme(_uuid uuid)
    LANGUAGE SQL AS
$$
    UPDATE skos_concept_scheme scheme SET deleted = true WHERE scheme.uuid = uuid;
$$;
