DROP FUNCTION IF EXISTS get_concept_semantic_relations_recursive;
CREATE OR REPLACE FUNCTION get_concept_semantic_relations_recursive(_uuid uuid, _type skos_semantic_relation_type, _depth int)
RETURNS SETOF skos_concept_ex
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
                                        INNER JOIN skos_concept_ex sc
                                                   ON sc.id = sr.source_id
                               WHERE sr.relation = _type
                                 AND sc.uuid = _uuid
                               UNION ALL
                               -- Then, iterate over the relationships of all `target_id`s.
                               SELECT sr.source_id,
                                      sr.target_id,
                                      sr.relation,
                                      TRUE AS transitive,
                                      rsr.depth + 1 AS depth,
                                      rsr.visited_ids || sr.source_id AS visited_ids,
                                      sr.source_id = ANY (rsr.visited_ids) AS is_cycle
                               FROM relationships rsr
                                        INNER JOIN skos_concept_semantic_relation sr
                                                   ON sr.relation = _type AND sr.source_id = rsr.target_id
                               WHERE NOT cycle
                                 AND rsr.depth <= _depth)
        SELECT sct.*
        FROM relationships r
                 inner join skos_concept_ex scs
                            ON scs.id = r.source_id
                 inner join skos_concept_ex sct
                            ON sct.id = r.target_id;
END;
$$ LANGUAGE plpgsql;
