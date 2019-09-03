CREATE OR REPLACE PROCEDURE create_or_update_concept_scheme(_uuid uuid,
                                                            _source varchar,
                                                            _title rdf_plain_literal)
    LANGUAGE plpgsql
AS
$$
BEGIN
    LOOP
        -- First attempt to update an existing scheme by its UUID.
        UPDATE skos_concept_scheme
        SET title = _title
        WHERE uuid = _uuid OR source = _source;

        -- If a record was updated, we're done here.
        IF found THEN
            RETURN;
        END IF;

        -- If not, we try to create a record based on the source or UUID.
        -- We do it in a loop so we don't race against others calling create_concept_scheme()
        BEGIN
            -- Attempt to create a concept scheme based on the source first.
            INSERT INTO skos_concept_scheme (uuid, source, title)
            VALUES (_uuid, _source, _title)
            ON CONFLICT (source) DO UPDATE
                SET title  = _title;
            RETURN;
        EXCEPTION
            WHEN unique_violation THEN
                BEGIN
                    INSERT INTO skos_concept_scheme (uuid, source, title)
                    VALUES (_uuid, _source, _title)
                    ON CONFLICT (uuid) DO UPDATE
                        SET title  = _title;
                    RETURN;
                EXCEPTION
                    WHEN unique_violation THEN
                    -- Do nothing, and loop to try the UPDATE again.
                END;
        END;
    END LOOP;
END;
$$;
