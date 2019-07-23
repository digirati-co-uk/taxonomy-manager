CREATE OR REPLACE FUNCTION is_plain_literal(val jsonb) RETURNS boolean AS
$$
BEGIN
    RETURN NOT EXISTS(SELECT 1
                      FROM jsonb_each(val) AS j
                      WHERE jsonb_typeof(j.value) != 'string');
END;
$$ LANGUAGE plpgsql;
COMMENT ON FUNCTION is_plain_literal(val jsonb) IS 'Check if a JSONB value contains only an object with string values';

CREATE DOMAIN rdf_plain_literal AS jsonb CHECK (is_plain_literal(VALUE));
COMMENT ON DOMAIN rdf_plain_literal IS 'A JSONB object type containing a map of ISO 9601 language keys to localized string literals';

CREATE TYPE skos_semantic_relation_type AS ENUM (
    'broader',
    'narrower',
    'related');

CREATE TABLE skos_concept_scheme
(
    id    bigserial primary key,
    uuid  uuid unique not null,
    title rdf_plain_literal NOT NULL
);

CREATE TABLE skos_concept
(
    id              bigserial primary key,
    uuid            uuid UNIQUE                    NOT NULL,
    preferred_label rdf_plain_literal DEFAULT '{}' NOT NULL,
    alt_label       rdf_plain_literal DEFAULT '{}' NOT NULL,
    hidden_label    rdf_plain_literal DEFAULT '{}' NOT NULL,
    note            rdf_plain_literal DEFAULT '{}' NOT NULL,
    change_note     rdf_plain_literal DEFAULT '{}' NOT NULL,
    editorial_note  rdf_plain_literal DEFAULT '{}' NOT NULL,
    example         rdf_plain_literal DEFAULT '{}' NOT NULL,
    history_note    rdf_plain_literal DEFAULT '{}' NOT NULL,
    scope_note      rdf_plain_literal DEFAULT '{}' NOT NULL
);


CREATE TABLE skos_concept_scheme_concept
(
    concept_scheme_id bigint references skos_concept_scheme (id),
    concept_id        bigint references skos_concept (id),
    is_top_concept    boolean
);
CREATE UNIQUE INDEX ON skos_concept_scheme_concept(concept_scheme_id, concept_id);


CREATE TABLE skos_concept_semantic_relation
(
    relation   skos_semantic_relation_type,
    transitive boolean,
    source_id  bigint references skos_concept (id),
    target_id  bigint references skos_concept (id)
);
