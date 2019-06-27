create unique index skos_concept_remantion_relation__uniq
    on skos_concept_semantic_relation (relation, transitive, source_id, target_id);
