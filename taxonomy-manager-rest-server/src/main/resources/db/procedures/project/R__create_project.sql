DROP PROCEDURE IF EXISTS create_project;
CREATE OR REPLACE PROCEDURE create_project(_slug character varying, _title rdf_plain_literal)
    LANGUAGE SQL AS
$$
INSERT INTO project (slug, title)
VALUES (_slug,
        _title);
$$;
