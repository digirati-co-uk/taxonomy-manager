CREATE TABLE project
(
    id    bigserial primary key,
    slug  character varying(50) unique not null,
    title rdf_plain_literal
);

CREATE TABLE project_skos_concept_scheme
(
    project_id        bigint references project (id),
    concept_scheme_id bigint references skos_concept_scheme (id)
);
CREATE UNIQUE INDEX ON project_skos_concept_scheme(project_id, concept_scheme_id);
