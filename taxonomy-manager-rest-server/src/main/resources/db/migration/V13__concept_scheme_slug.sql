create or replace view skos_concept_scheme_ex as
select c.id, c.uuid, c.title,c.source, p.slug as project_slug
from skos_concept_scheme c
inner join project p on p.id = c.project_id
where c.deleted = false;
