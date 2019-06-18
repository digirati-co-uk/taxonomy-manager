CREATE PROCEDURE create_concept(_uuid uuid,
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
INSERT INTO skos_concept (
                         uuid,
                         preferred_label,
                         alt_label,
                         hidden_label,
                         note,
                         change_note,
                         editorial_note,
                         example,
                         history_note,
                         scope_note)
VALUES (
       _uuid,
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


CREATE OR REPLACE FUNCTION update_concept(_uuid uuid,
                                          _preferred_label jsonb,
                                          _alt_label jsonb,
                                          _hidden_label jsonb,
                                          _note jsonb,
                                          _change_note jsonb,
                                          _editorial_note jsonb,
                                          _example jsonb,
                                          _history_note jsonb,
                                          _scope_note jsonb) RETURNS VOID AS
$$
BEGIN
    UPDATE skos_concept
    SET preferred_label = _preferred_label,
        alt_label       = _alt_label,
        hidden_label    = _hidden_label,
        note            = _note,
        change_note     = _change_note,
        editorial_note  = _editorial_note,
        example         = _example,
        history_note    = _history_note,
        scope_note      = _scope_note
    WHERE uuid = _uuid;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_concept(uniqid UUID) RETURNS SETOF skos_concept AS
$$
BEGIN
    RETURN QUERY
        SELECT concept.* FROM skos_concept concept WHERE concept.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;

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

CREATE OR REPLACE FUNCTION update_concept_scheme(_uuid uuid,
                                                 _title rdf_plain_literal) RETURNS VOID AS
$$
BEGIN
    UPDATE skos_concept_scheme
    SET title = _title
    WHERE uuid = _uuid;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_concept_scheme(uniqid UUID) RETURNS SETOF skos_concept_scheme AS
$$
BEGIN
    RETURN QUERY
        SELECT scheme.* FROM skos_concept_scheme scheme WHERE scheme.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_concept_scheme_top_concepts(uniqid UUID)
    RETURNS TABLE
            (
                uuid            uuid,
                preferred_label rdf_plain_literal
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT c.uuid, c.preferred_label
        FROM skos_concept_scheme_concept sc_entry
                 INNER JOIN skos_concept_scheme cs ON cs.id = sc_entry.concept_scheme_id
                 INNER JOIN skos_concept c ON c.id = sc_entry.concept_id
        WHERE cs.uuid = uniqid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_concept_relationships(source_uuid UUID)
    RETURNS TABLE
            (
                target_uuid            uuid,
                target_preferred_label rdf_plain_literal,
                relation               skos_semantic_relation_type,
                transitive             boolean
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT tc.uuid            AS target_uuid,
               tc.preferred_label AS target_preferred_label,
               relation.relation,
               relation.transitive
        FROM skos_concept_semantic_relation relation
                 INNER JOIN skos_concept sc on relation.source_id = sc.id
                 INNER JOIN skos_concept tc ON relation.target_id = tc.id
        WHERE sc.uuid = source_uuid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE function get_project(_slug character varying) RETURNS SETOF project AS
$$
BEGIN
    RETURN QUERY
        SELECT project.* FROM project WHERE project.slug = _slug;
END
$$ LANGUAGE plpgsql;
