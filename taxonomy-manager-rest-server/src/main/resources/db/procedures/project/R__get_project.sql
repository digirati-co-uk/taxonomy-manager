DROP FUNCTION IF EXISTS get_project;
create function get_project(_slug character varying) returns SETOF project_ex
    language plpgsql
as
$$
begin
    return QUERY
        select project.* from project_ex project where project.slug = _slug;
end
$$;
