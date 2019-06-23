DROP PROCEDURE IF EXISTS create_concept_scheme;
CREATE OR REPLACE PROCEDURE create_concept_scheme(_uuid uuid,
                                                  _title rdf_plain_literal) LANGUAGE SQL AS
$$
INSERT INTO skos_concept_scheme (
    uuid,
    title)
VALUES (
           _uuid,
           _title);
$$;
