create or replace view skos_concept_ex as
select * from skos_concept where deleted = false;
