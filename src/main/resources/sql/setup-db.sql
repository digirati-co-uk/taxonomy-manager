-- TODO work out what to do about setting up DBs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE semantic_relation_type AS ENUM (
    'broader',
    'narrower',
    'related',
    'in_scheme',
    'has_top_concept',
    'top_concept_of');

CREATE TABLE concept_scheme
(
  id uuid primary key
);

CREATE TABLE concept
(
  id              uuid primary key,
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
  source_id  uuid,
  target_id  uuid
);
