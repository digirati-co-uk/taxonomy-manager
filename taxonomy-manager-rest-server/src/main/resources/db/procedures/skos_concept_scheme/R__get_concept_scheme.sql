DROP FUNCTION IF EXISTS get_concept_scheme(UUID);
CREATE OR REPLACE FUNCTION get_concept_scheme(uniqid UUID) RETURNS SETOF skos_concept_scheme_ex AS
$$
BEGIN
    RETURN QUERY
        SELECT scheme.* FROM skos_concept_scheme_ex scheme WHERE scheme.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;
