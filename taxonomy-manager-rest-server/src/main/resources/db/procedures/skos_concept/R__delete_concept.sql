DROP PROCEDURE IF exists delete_concept;
DROP FUNCTION IF EXISTS delete_concept;

CREATE OR REPLACE PROCEDURE delete_concept(_uuid uuid)
    LANGUAGE plpgsql
AS
$$
BEGIN
    UPDATE skos_concept concept SET deleted = true WHERE concept.uuid = _uuid;
END;
$$;
