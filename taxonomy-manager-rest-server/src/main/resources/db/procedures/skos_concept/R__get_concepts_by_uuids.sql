DROP FUNCTION IF EXISTS get_concepts_by_uuids;
CREATE OR REPLACE FUNCTION get_concepts_by_uuids(_uuids UUID[]) RETURNS SETOF skos_concept_ex AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.*
        FROM skos_concept_ex concept
        WHERE concept.uuid = ANY(_uuids);
END;
$$ LANGUAGE plpgsql;
