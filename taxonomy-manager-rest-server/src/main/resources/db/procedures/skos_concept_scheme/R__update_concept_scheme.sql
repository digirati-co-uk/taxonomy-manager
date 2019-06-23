CREATE OR REPLACE FUNCTION update_concept_scheme(_uuid uuid,
                                                 _title rdf_plain_literal) RETURNS VOID AS
$$
BEGIN
    UPDATE skos_concept_scheme
    SET title = _title
    WHERE uuid = _uuid;
END;
$$ LANGUAGE plpgsql;
