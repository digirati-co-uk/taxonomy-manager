CREATE OR REPLACE FUNCTION get_concept_semantic_relations_recursive(source_uuid uuid, relation_type skos_semantic_relation_type)
RETURNS TABLE (
    source_uuid            uuid,
    target_uuid            uuid,
    target_preferred_label rdf_plain_literal,
    relation               skos_semantic_relation_type,
    transitive             boolean
)
AS
$$
BEGIN
    RETURN QUERY
        WITH RECURSIVE relationships (source_id, target_id, relation, transitive) AS (
            -- First, select all the top-level relationships for our `source_id`.
            SELECT sr.target_id,
                   sr.relation,
                   sr.transitive,
                   1 as depth
            FROM skos_concept_semantic_relation sr
                INNER JOIN skos_concept sc
                    ON sc.id = sr.source_id
            WHERE sc.uuid = source_uuid
              AND sr.relation = relation_type
            UNION ALL
            -- Then, iterate over the relationships of all `target_id`s.
            SELECT sr.target_id,
                   sr.relation,
                   sr.transitive,
                   rsr.depth + 1 as depth
            FROM skos_concept_semantic_relation sr, relationships rsr
            WHERE sr.source_id = rsr.target_id
              AND sr.relation = relation_type
        )
        SELECT scs.uuid as source_uuid,
               sct.uuid as target_uuid,
               sct.preferred_label as target_preferred_label,
               r.transitive,
               r.relation
        FROM relationships r
            INNER JOIN skos_concept scs
                ON scs.id = r.source_id
            INNER JOIN skos_concept sct
                ON sct.id = r.target_id;
END;
$$ LANGUAGE plpgsql;
