DROP PROCEDURE IF EXISTS create_or_update_concept_scheme(uuid, varchar, rdf_plain_literal);
DROP FUNCTION IF EXISTS create_or_update_concept_scheme(uuid, varchar, rdf_plain_literal);

CREATE OR REPLACE FUNCTION create_or_update_concept_scheme(_uuid uuid,
                                                           _projectslug varchar,
                                                           _source varchar,
                                                           _title rdf_plain_literal)
    RETURNS SETOF skos_concept_scheme_ex
    LANGUAGE plpgsql
AS
$$
BEGIN

    RETURN QUERY
        INSERT INTO skos_concept_scheme (uuid, project_id, source, title)
            VALUES (
                   _uuid,
                   (SELECT id FROM project WHERE slug = _projectslug),
                   _source,
                   _title)
            ON CONFLICT (project_id, uuid, COALESCE(source, '')) DO UPDATE SET title = _title
            RETURNING id, uuid, title, source, deleted, _projectslug::varchar(50) as project_slug;
EXCEPTION
    WHEN unique_violation
        THEN RAISE unique_violation USING MESSAGE =
                'Concept Scheme violates the unique composite key of (uuid, source)';

END;
$$;
