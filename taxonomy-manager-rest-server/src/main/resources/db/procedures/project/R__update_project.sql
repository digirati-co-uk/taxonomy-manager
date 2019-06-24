DROP PROCEDURE IF EXISTS update_project;
CREATE OR REPLACE PROCEDURE update_project(_slug character varying, _title rdf_plain_literal)
    LANGUAGE SQL AS
$$
UPDATE project SET title = _title WHERE slug = _slug;
$$;
