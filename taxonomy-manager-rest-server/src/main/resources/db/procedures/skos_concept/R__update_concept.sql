DROP PROCEDURE IF exists update_concept;
DROP FUNCTION IF EXISTS update_concept;

CREATE OR REPLACE PROCEDURE update_concept(_uuid uuid,
                                           _source varchar,
                                           _preferred_label jsonb,
                                           _alt_label jsonb,
                                           _hidden_label jsonb,
                                           _note jsonb,
                                           _change_note jsonb,
                                           _editorial_note jsonb,
                                           _example jsonb,
                                           _history_note jsonb,
                                           _scope_note jsonb,
                                           _definition jsonb)
    LANGUAGE plpgsql
AS
$$
BEGIN
    LOOP
        UPDATE skos_concept c
        SET source          = _source,
            preferred_label = _preferred_label,
            alt_label       = _alt_label,
            hidden_label    = _hidden_label,
            note            = _note,
            change_note     = _change_note,
            editorial_note  = _editorial_note,
            example         = _example,
            history_note    = _history_note,
            scope_note      = _scope_note,
            definition      = _definition
        WHERE source = _source
          AND uuid = _uuid;
        IF found THEN
            RETURN;
        END IF;

        -- not there, so try to insert the key
        -- if someone else inserts the same key concurrently, or key2
        -- already exists in col2,
        -- we could get a unique-key failure
        BEGIN
            INSERT INTO skos_concept AS c (
                                          uuid,
                                          source,
                                          preferred_label,
                                          alt_label,
                                          hidden_label,
                                          note,
                                          change_note,
                                          editorial_note,
                                          example,
                                          history_note,
                                          scope_note,
                                          definition)
            VALUES (
                   _uuid,
                   _source,
                   _preferred_label,
                   _alt_label,
                   _hidden_label,
                   _note,
                   _change_note,
                   _editorial_note,
                   _example,
                   _history_note,
                   _scope_note,
                   _definition)
            ON CONFLICT (source) DO UPDATE
                SET source          = _source,
                    preferred_label = _preferred_label,
                    alt_label       = _alt_label,
                    hidden_label    = _hidden_label,
                    note            = _note,
                    change_note     = _change_note,
                    editorial_note  = _editorial_note,
                    example         = _example,
                    history_note    = _history_note,
                    scope_note      = _scope_note,
                    definition      = _definition;
            RETURN;
        EXCEPTION
            WHEN unique_violation THEN
                BEGIN
                    INSERT INTO skos_concept AS c (
                                                  uuid,
                                                  source,
                                                  preferred_label,
                                                  alt_label,
                                                  hidden_label,
                                                  note,
                                                  change_note,
                                                  editorial_note,
                                                  example,
                                                  history_note,
                                                  scope_note,
                                                  definition)
                    VALUES (
                           _uuid,
                           _source,
                           _preferred_label,
                           _alt_label,
                           _hidden_label,
                           _note,
                           _change_note,
                           _editorial_note,
                           _example,
                           _history_note,
                           _scope_note,
                           _definition)
                    ON CONFLICT (uuid) DO UPDATE
                        SET source          = _source,
                            preferred_label = _preferred_label,
                            alt_label       = _alt_label,
                            hidden_label    = _hidden_label,
                            note            = _note,
                            change_note     = _change_note,
                            editorial_note  = _editorial_note,
                            example         = _example,
                            history_note    = _history_note,
                            scope_note      = _scope_note,
                            definition      = _definition;
                    RETURN;
                EXCEPTION
                    WHEN unique_violation THEN
                    -- Do nothing, and loop to try the UPDATE again.
                END;
        END;
    END LOOP;
END;
$$;
