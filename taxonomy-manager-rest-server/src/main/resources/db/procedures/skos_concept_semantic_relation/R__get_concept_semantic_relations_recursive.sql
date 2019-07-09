CREATE OR REPLACE FUNCTION get_concept_semantic_relations_recursive(_uuid uuid, _type skos_semantic_relation_type)
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
        WITH RECURSIVE relationships (source_id, target_id, relation, transitive, depth, visited_ids, cycle) AS
                           (
                               -- First, select all the top-level relationships for our `source_id`.
                               SELECT sr.source_id,
                                      sr.target_id,
                                      sr.relation,
                                      sr.transitive,
                                      1                    AS depth,
                                      ARRAY [sr.source_id] AS visited_ids,
                                      FALSE                AS is_cycle
                               FROM skos_concept_semantic_relation sr
                                        INNER JOIN skos_concept sc
                                                   ON sc.id = sr.source_id
                               WHERE sr.relation = _type
                                 AND sc.uuid = _uuid
                               UNION ALL
                               -- Then, iterate over the relationships of all `target_id`s.
                               SELECT sr.source_id,
                                      sr.target_id,
                                      sr.relation,
                                      TRUE,
                                      rsr.depth + 1 AS depth,
                                      rsr.visited_ids || sr.source_id,
                                      sr.source_id = ANY (rsr.visited_ids)
                               FROM relationships rsr
                                        INNER JOIN skos_concept_semantic_relation sr
                                                   ON sr.relation = _type AND sr.source_id = rsr.target_id
                               WHERE NOT cycle
                                 AND rsr.depth < 5)
        SELECT scs.uuid            AS source_uuid,
               sct.uuid            AS target_uuid,
               sct.preferred_label AS target_preferred_label,
               r.relation,
               r.transitive,
               r.depth
        FROM relationships r
                 inner join skos_concept scs
                            ON scs.id = r.source_id
                 inner join skos_concept sct
                            ON sct.id = r.target_id;
END;
$$ LANGUAGE plpgsql;
