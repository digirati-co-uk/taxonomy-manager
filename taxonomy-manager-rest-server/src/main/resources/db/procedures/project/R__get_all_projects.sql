DROP FUNCTION IF EXISTS get_all_projects();
CREATE OR REPLACE FUNCTION get_all_projects() RETURNS SETOF project_ex AS
$$
BEGIN
    RETURN QUERY
        SELECT project.* FROM project_ex project;
END;
$$ LANGUAGE plpgsql;
