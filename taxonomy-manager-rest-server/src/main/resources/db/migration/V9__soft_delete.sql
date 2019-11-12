-- CONCEPT

alter table skos_concept
    add deleted boolean default false not null;

create or replace view skos_concept_ex as
    select * from skos_concept where deleted = false;

create index skos_concept_uuid_key_not_deleted
    on skos_concept (uuid)
    where deleted = false;

create index skos_concept__uniq_source_not_deleted
    on skos_concept (source)
    where deleted = false;

create index skos_concept_pkey_not_deleted
    on skos_concept (id)
    where deleted = false;


-- PROJECT

alter table project
    add deleted boolean default false not null;

create or replace view project_ex as
    select * from project where deleted = false;

create index project_slug_key_not_deleted
    on project (slug)
    where deleted = false;

create index project_pkey_not_deleted
    on project (id)
    where deleted = false;

-- CONCEPT SCHEME

alter table skos_concept_scheme
    add deleted boolean default false not null;

create or replace view skos_concept_scheme_ex as
    select * from skos_concept_scheme where deleted = false;

create index skos_concept_scheme_uuid_key_not_deleted
    on skos_concept_scheme (uuid)
    where deleted = false;

create index skos_concept_scheme__uniq_source_not_deleted
    on skos_concept_scheme (source)
    where deleted = false;

create index skos_concept_scheme_pkey_not_deleted
    on skos_concept_scheme (id)
    where deleted = false;
