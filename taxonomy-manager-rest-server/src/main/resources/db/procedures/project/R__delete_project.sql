drop procedure IF EXISTS delete_project(_slug character varying);
create or replace procedure delete_project(_slug character varying)
    LANGUAGE SQL AS
$$
UPDATE project SET deleted = true WHERE project.slug = _slug;
$$;
