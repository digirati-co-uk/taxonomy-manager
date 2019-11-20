create or replace function get_project_concept_schemes(_slug character varying)
RETURNS TABLE (uuid uuid, title rdf_plain_literal, source varchar)
as
$$
begin
    return QUERY
        select cs.uuid, cs.title, cs.source
        from project_skos_concept_scheme pcs
                inner join project_ex p on pcs.project_id = p.id
                inner join skos_concept_scheme_ex cs on pcs.concept_scheme_id = cs.id
        where p.slug = _slug;
end;
$$ LANGUAGE plpgsql;
