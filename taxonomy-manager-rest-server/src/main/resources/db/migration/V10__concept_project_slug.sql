-- Make the concept view include project slug, and only return non-deleted entries
-- for non-deleted concept schemes in non-deleted projects
create or replace view skos_concept_ex as
    select sc.*, p.slug
        from skos_concept sc
        inner join skos_concept_scheme_concept scsc on sc.id = scsc.concept_id
        inner join skos_concept_scheme scs on scsc.concept_scheme_id = scs.id
        inner join project_skos_concept_scheme pscs on scsc.concept_scheme_id = scs.id
        inner join project p on pscs.project_id = p.id
    where sc.deleted = false
    and scs.deleted = false
    and p.deleted = false;
