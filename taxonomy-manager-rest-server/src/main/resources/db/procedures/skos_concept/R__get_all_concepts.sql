DROP FUNCTION IF EXISTS get_all_concepts;
CREATE OR REPLACE FUNCTION get_all_concepts() RETURNS SETOF skos_concept_Ex AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.* FROM skos_concept_ex concept;
END;
$$ LANGUAGE plpgsql;
