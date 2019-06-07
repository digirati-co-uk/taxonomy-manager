-- TODO work out what to do about setting up DBs

CREATE TYPE semantic_relation_type AS ENUM (
    'broader',
    'narrower',
    'related',
    'in_scheme',
    'has_top_concept',
    'top_concept_of');

CREATE TABLE concept_scheme
(
  id  bigserial primary key,
  iri text
);

CREATE TABLE concept
(
  id              bigserial primary key,
  iri             text,
  preferred_label jsonb,
  alt_label       jsonb,
  hidden_label    jsonb,
  note            jsonb,
  change_note     jsonb,
  editorial_note  jsonb,
  example         jsonb,
  history_note    jsonb,
  scope_note      jsonb
);

CREATE TABLE concept_semantic_relation
(
  relation   semantic_relation_type,
  transitive boolean,
  source_id  bigint,
  target_id  bigint,
  source_iri text,
  target_iri text
);
