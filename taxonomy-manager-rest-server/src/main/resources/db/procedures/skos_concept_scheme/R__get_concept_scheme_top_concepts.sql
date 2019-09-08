DROP FUNCTION IF EXISTS get_concept_scheme_top_concepts;
CREATE OR REPLACE FUNCTION get_concept_scheme_top_concepts(uniqid UUID)
    RETURNS TABLE
            (
                uuid            uuid,
                source          varchar,
                preferred_label rdf_plain_literal
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT c.uuid, c.source, c.preferred_label
        FROM skos_concept_scheme_concept sc_entry
                 INNER JOIN skos_concept_scheme cs ON cs.id = sc_entry.concept_scheme_id
                 INNER JOIN skos_concept c ON c.id = sc_entry.concept_id
        WHERE cs.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;
