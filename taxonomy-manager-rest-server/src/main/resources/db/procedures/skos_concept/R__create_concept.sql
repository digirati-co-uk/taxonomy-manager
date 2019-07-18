DROP PROCEDURE IF EXISTS create_concept;
CREATE OR REPLACE PROCEDURE create_concept(_uuid uuid,
                                           _source varchar,
                                           _preferred_label rdf_plain_literal,
                                           _alt_label rdf_plain_literal,
                                           _hidden_label rdf_plain_literal,
                                           _note rdf_plain_literal,
                                           _change_note rdf_plain_literal,
                                           _editorial_note rdf_plain_literal,
                                           _example rdf_plain_literal,
                                           _history_note rdf_plain_literal,
                                           _scope_note rdf_plain_literal)
    LANGUAGE SQL
AS
$$
INSERT INTO skos_concept (uuid,
                          source,
                          preferred_label,
                          alt_label,
                          hidden_label,
                          note,
                          change_note,
                          editorial_note,
                          example,
                          history_note,
                          scope_note)
VALUES (_uuid,
        _source,
        _preferred_label,
        _alt_label,
        _hidden_label,
        _note,
        _change_note,
        _editorial_note,
        _example,
        _history_note,
        _scope_note);
$$;
