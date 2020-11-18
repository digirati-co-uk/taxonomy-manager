create or replace view skos_concept_ex as
select c.*, p.slug as project_slug
from skos_concept c
inner join project p on p.id = c.project_id
where c.deleted = false;
