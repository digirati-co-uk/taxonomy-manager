DROP PROCEDURE IF EXISTS create_or_update_project(character varying, rdf_plain_literal);
CREATE OR REPLACE PROCEDURE create_or_update_project(_slug character varying, _title rdf_plain_literal)
    LANGUAGE SQL AS
$$
INSERT INTO project (slug, title)
VALUES (_slug,
        _title)
ON CONFLICT (slug) DO UPDATE
    SET title = _title;
$$;
