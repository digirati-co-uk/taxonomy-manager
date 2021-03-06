package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.PersistentProjectScopedModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelContext;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Multimap;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.UUID;
import java.util.stream.Stream;

@RdfType("http://www.w3.org/2004/02/skos/core#Concept")
@RdfContext(value = {"skos=" + SKOS.uri, "dcterms=" + DCTerms.NS}, template = "/v0.1/concept/:id:")
public final class ConceptModel implements Concept, RdfModel, PersistentProjectScopedModel {

    private final RdfModelContext context;
    private UUID uuid;
    private String projectId;

    @RdfConstructor
    public ConceptModel(RdfModelContext context) {
        this.context = context;
        this.uuid = null;
    }

    public ConceptModel(UUID uuid, RdfModelContext context) {
        this.uuid = uuid;
        this.context = context;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    // @TODO: derive this from the URI at mapping from Resource time
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getSource() {
        Resource sourceResource = getResource().getPropertyResourceValue(DCTerms.source);
        try {
            return sourceResource == null ? getStringProperty(DCTerms.source) : sourceResource.getURI();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public RdfModelContext getContext() {
        return context;
    }

    @Override
    public Multimap<String, String> getPreferredLabel() {
        return getPlainLiteral(SKOS.prefLabel);
    }

    @Override
    public Multimap<String, String> getAltLabel() {
        return getPlainLiteral(SKOS.altLabel);
    }

    @Override
    public Multimap<String, String> getHiddenLabel() {
        return getPlainLiteral(SKOS.hiddenLabel);
    }

    @Override
    public Multimap<String, String> getNote() {
        return getPlainLiteral(SKOS.note);
    }

    @Override
    public Multimap<String, String> getChangeNote() {
        return getPlainLiteral(SKOS.changeNote);
    }

    @Override
    public Multimap<String, String> getEditorialNote() {
        return getPlainLiteral(SKOS.editorialNote);
    }

    @Override
    public Multimap<String, String> getExample() {
        return getPlainLiteral(SKOS.example);
    }

    @Override
    public Multimap<String, String> getHistoryNote() {
        return getPlainLiteral(SKOS.historyNote);
    }

    @Override
    public Multimap<String, String> getScopeNote() {
        return getPlainLiteral(SKOS.scopeNote);
    }

    @Override
    public Multimap<String, String> getDefinition() {
        return getPlainLiteral(SKOS.definition);
    }

    /**
     * Get a stream of all the {@link Resource}s that are related to this model with
     * the given relationship {@code type}.
     *
     * @param type       The type of relationship to search for.
     * @param transitive If transitive relationships should be searched for, rather than non-transitive relationships.
     * @return A stream of all the resources related to this {@link ConceptModel}.
     */
    public Stream<ConceptModel> getRelationships(ConceptRelationshipType type, boolean transitive) {
        var skosProperty = type.getSkosProperty(transitive);

        return getResources(ConceptModel.class, skosProperty);

    }

    @Override
    public String getProjectId() {
        return projectId;
    }

    @Override
    public void setProjectId(String id) {
        this.projectId = id;
    }
}
