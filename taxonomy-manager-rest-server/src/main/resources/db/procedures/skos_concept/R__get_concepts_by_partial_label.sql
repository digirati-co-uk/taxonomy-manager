DROP FUNCTION IF EXISTS get_concepts_by_partial_label;
CREATE OR REPLACE FUNCTION get_concepts_by_partial_label(_label text, _language text) RETURNS SETOF skos_concept AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.*
        FROM skos_concept concept
        WHERE contains_label_prefix(concept.preferred_label, _label, _language)
        OR contains_label_prefix(concept.alt_label, _label, _language)
        OR contains_label_prefix(concept.hidden_label, _label, _language);
END
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS contains_label_prefix;
CREATE OR REPLACE FUNCTION contains_label_prefix(_json jsonb, _label text, _language text) RETURNS boolean AS
$$
BEGIN
    RETURN EXISTS(SELECT 1
                  FROM json_array_elements_text((_json -> _language)::json) value
                  WHERE value::text ~ ('^' || _label));
END
$$ LANGUAGE plpgsql;
