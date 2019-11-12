CREATE OR REPLACE FUNCTION create_or_update_concept_scheme(_uuid uuid,
                                                           _source varchar,
                                                           _title rdf_plain_literal)
    RETURNS SETOF skos_concept_scheme
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY
        INSERT INTO skos_concept_scheme(uuid,source,title)
        VALUES (_uuid, _source, _title)
        ON CONFLICT (uuid, COALESCE(source,'')) DO UPDATE SET title = _title
        RETURNING *;
    EXCEPTION
        WHEN unique_violation
        THEN RAISE unique_violation USING MESSAGE =
            'Concept Scheme violates the unique composite key of (uuid, source)';

END;
$$;
