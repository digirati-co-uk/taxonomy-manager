DROP FUNCTION IF EXISTS get_concept;
CREATE OR REPLACE FUNCTION get_concept(uniqid UUID) RETURNS SETOF skos_concept_ex AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.* FROM skos_concept_ex concept WHERE concept.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;
