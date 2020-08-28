DROP FUNCTION IF EXISTS get_concepts_by_uuids;
CREATE OR REPLACE FUNCTION get_concepts_by_uuids(
        _uuids UUID[],
        _projectSlug character varying(50),
        _conceptSchemeUuid UUID
    )
    RETURNS SETOF skos_concept_ex AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.*
        FROM skos_concept_ex concept
        WHERE concept.uuid = ANY(_uuids)
        AND (_projectSlug IS NULL OR concept.slug = _projectSlug)
        AND (_conceptSchemeUuid IS NULL OR _conceptSchemeUuid = concept.schemeUuid);
END;
$$ LANGUAGE plpgsql;
