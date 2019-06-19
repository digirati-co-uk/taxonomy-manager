package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RdfType("http://www.w3.org/2004/02/skos/core#Concept")
public final class ConceptRdfModel implements RdfModel {

    private final Resource resource;
    private UUID uuid;

    @RdfConstructor
    public ConceptRdfModel(Resource resource) {
        this.resource = resource;
        this.uuid = null;
    }

    public ConceptRdfModel(UUID uuid, Resource resource) {
        this.uuid = uuid;
        this.resource = resource;
    }

    public Optional<UUID> getUuid() {
        return Optional.ofNullable(uuid);
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isNew() {
        return uuid == null;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public Map<String, String> getPreferredLabel() {
        return getPlainLiteral(SKOS.prefLabel);
    }

    public Map<String, String> getAltLabel() {
        return getPlainLiteral(SKOS.altLabel);
    }

    public Map<String, String> getHiddenLabel() {
        return getPlainLiteral(SKOS.hiddenLabel);
    }

    public Map<String, String> getNote() {
        return getPlainLiteral(SKOS.note);
    }

    public Map<String, String> getChangeNote() {
        return getPlainLiteral(SKOS.changeNote);
    }

    public Map<String, String> getEditorialNote() {
        return getPlainLiteral(SKOS.editorialNote);
    }

    public Map<String, String> getExample() {
        return getPlainLiteral(SKOS.example);
    }

    public Map<String, String> getHistoryNote() {
        return getPlainLiteral(SKOS.historyNote);
    }

    public Map<String, String> getScopeNote() {
        return getPlainLiteral(SKOS.scopeNote);
    }
}
