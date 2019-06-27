CREATE OR REPLACE function get_project(_slug character varying) RETURNS SETOF project AS
$$
BEGIN
    RETURN QUERY
        SELECT project.* FROM project WHERE project.slug = _slug;
END
$$ LANGUAGE plpgsql;
