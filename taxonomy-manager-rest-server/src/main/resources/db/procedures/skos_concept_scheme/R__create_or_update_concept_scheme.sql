CREATE OR REPLACE PROCEDURE create_or_update_concept_scheme(_uuid uuid,
                                                            _source varchar,
                                                            _title rdf_plain_literal) LANGUAGE SQL AS
$$
INSERT INTO skos_concept_scheme (uuid, source, title)
VALUES (_uuid,
        _source,
        _title)
ON CONFLICT (uuid) DO UPDATE
    SET title = _title,
        source = _source;
$$;
