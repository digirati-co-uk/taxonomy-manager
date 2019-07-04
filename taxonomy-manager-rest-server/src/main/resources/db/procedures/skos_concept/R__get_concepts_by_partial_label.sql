DROP FUNCTION IF EXISTS get_concepts_by_partial_label;
CREATE OR REPLACE FUNCTION get_concepts_by_partial_label(_label text) RETURNS SETOF skos_concept AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.*
        FROM skos_concept concept
        WHERE EXISTS(SELECT 1
                     FROM jsonb_each_text(concept.preferred_label) AS preferred_label
                     WHERE preferred_label.value::text ~ ('^' || _label));
END
$$ LANGUAGE plpgsql;
