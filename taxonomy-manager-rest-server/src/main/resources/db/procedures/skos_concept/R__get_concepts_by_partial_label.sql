DROP FUNCTION IF EXISTS get_concepts_by_partial_label;
CREATE OR REPLACE FUNCTION get_concepts_by_partial_label(_label text, _language text) RETURNS SETOF skos_concept_ex AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.*
        FROM skos_concept_ex concept
        WHERE contains_label_prefix(concept.preferred_label, _label, _language)
        OR contains_label_prefix(concept.alt_label, _label, _language)
        OR contains_label_prefix(concept.hidden_label, _label, _language);
END
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS contains_label_prefix;
CREATE OR REPLACE FUNCTION contains_label_prefix(_json jsonb, _label text, _language text) RETURNS boolean AS
$$
BEGIN
    IF jsonb_typeof(_json -> _language) = 'array' THEN
        RETURN EXISTS(SELECT 1
                      FROM json_array_elements_text((_json -> _language)::json) value
                      WHERE value::text ~ ('^' || _label));
    ELSE
        RETURN EXISTS(SELECT 1
                      FROM jsonb_each_text(_json) AS preferred_label
                      WHERE preferred_label.key = _language
                        AND preferred_label.value::text ~ ('^' || _label));
    END IF;
END
$$ LANGUAGE plpgsql;
