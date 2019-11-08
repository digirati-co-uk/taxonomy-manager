DROP FUNCTION IF EXISTS get_concept_relationships;
CREATE OR REPLACE FUNCTION get_concept_relationships(_source_uuid UUID)
RETURNS TABLE (
    source_uuid            uuid,
    target_uuid            uuid,
    target_preferred_label rdf_plain_literal,
    target_source          varchar,
    relation               skos_semantic_relation_type,
    transitive             boolean
)
AS
$$
BEGIN
    RETURN QUERY
        SELECT sc.uuid            AS source_uuid,
               tc.uuid            AS target_uuid,
               tc.preferred_label AS target_preferred_label,
               tc.source          AS target_source,
               relation.relation,
               relation.transitive
        FROM skos_concept_semantic_relation relation
            INNER JOIN skos_concept_ex sc
                ON relation.source_id = sc.id
            INNER JOIN skos_concept_ex tc
                ON relation.target_id = tc.id
        WHERE sc.uuid = _source_uuid;
END;
$$ LANGUAGE plpgsql;
