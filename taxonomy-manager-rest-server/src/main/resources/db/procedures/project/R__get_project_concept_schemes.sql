CREATE OR REPLACE FUNCTION get_project_concept_schemes(_slug character varying)
RETURNS TABLE (uuid uuid, title rdf_plain_literal)
AS
$$
BEGIN
    RETURN QUERY
        SELECT cs.uuid, cs.title
        FROM project_skos_concept_scheme pcs
                INNER JOIN project p ON pcs.project_id = p.id
                INNER JOIN skos_concept_scheme cs ON pcs.concept_scheme_id = cs.id
        WHERE p.slug = _slug;
END;
$$ LANGUAGE plpgsql;
