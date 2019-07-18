DROP FUNCTION IF EXISTS get_all_projects();
CREATE OR REPLACE FUNCTION get_all_projects() RETURNS SETOF project AS
$$
BEGIN
    RETURN QUERY
        SELECT project.* FROM project project;
END;
$$ LANGUAGE plpgsql;
