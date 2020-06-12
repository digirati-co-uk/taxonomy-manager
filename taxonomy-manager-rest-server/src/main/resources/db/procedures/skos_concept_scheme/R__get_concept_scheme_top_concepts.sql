DROP FUNCTION IF EXISTS get_concept_scheme_top_concepts;
CREATE OR REPLACE FUNCTION get_concept_scheme_top_concepts(uniqid UUID)
    RETURNS SETOF skos_concept_ex
AS
$$
BEGIN
    RETURN QUERY
        SELECT c.*
        FROM skos_concept_scheme_concept sc_entry
                 INNER JOIN skos_concept_scheme_ex cs ON cs.id = sc_entry.concept_scheme_id
                 INNER JOIN skos_concept_ex c ON c.id = sc_entry.concept_id
        WHERE cs.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;
