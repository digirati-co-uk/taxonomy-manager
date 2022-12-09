create or replace view skos_concept_ex as
select
  c.*,
  p.slug as project_slug,
  array_to_json(
    array(
      select
        json_build_object(
          'uuid', scs.uuid, 'title', scs.title
        )
      from
        skos_concept_scheme_concept relation
        inner join skos_concept_scheme scs on relation.concept_scheme_id = scs.id
      where
        relation.concept_id = c.id AND scs.deleted = false AND relation.is_top_concept = true
    )
  ) as top_concept_of
from
  skos_concept c
  inner join project p on p.id = c.project_id
where
  c.deleted = false;
