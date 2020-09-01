ALTER TABLE skos_concept
    ADD COLUMN definition rdf_plain_literal DEFAULT '{}' NOT NULL;
