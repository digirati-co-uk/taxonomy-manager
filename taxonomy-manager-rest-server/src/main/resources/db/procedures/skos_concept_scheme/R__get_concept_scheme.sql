CREATE OR REPLACE FUNCTION get_concept_scheme(uniqid UUID) RETURNS SETOF skos_concept_scheme AS
$$
BEGIN
    RETURN QUERY
        SELECT scheme.* FROM skos_concept_scheme scheme WHERE scheme.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;
