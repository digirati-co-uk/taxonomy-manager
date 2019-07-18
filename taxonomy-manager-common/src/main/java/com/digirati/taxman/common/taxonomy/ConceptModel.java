package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.PersistentModel;
import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@RdfType("http://www.w3.org/2004/02/skos/core#Concept")
@RdfContext({"skos=" + SKOS.uri, "dcterms=" + DCTerms.NS})
public final class ConceptModel implements RdfModel, PersistentModel, Concept {

    private final Resource resource;
    private UUID uuid;

    @RdfConstructor
    public ConceptModel(Resource resource) {
        this.resource = resource;
        this.uuid = null;
    }

    public ConceptModel(UUID uuid, Resource resource) {
        this.uuid = uuid;
        this.resource = resource;
    }

    @Override
    public UUID getUuid() {
        if (uuid == null) {
            throw new IllegalStateException("Concept schemes must have a UUID.");
        }
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getSource() {
        Statement sourceStatement = resource.getProperty(DCTerms.source);
        return sourceStatement == null ? null : sourceStatement.getString();
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public Map<String, String> getPreferredLabel() {
        return getPlainLiteral(SKOS.prefLabel);
    }

    @Override
    public Map<String, String> getAltLabel() {
        return getPlainLiteral(SKOS.altLabel);
    }

    @Override
    public Map<String, String> getHiddenLabel() {
        return getPlainLiteral(SKOS.hiddenLabel);
    }

    @Override
    public Map<String, String> getNote() {
        return getPlainLiteral(SKOS.note);
    }

    @Override
    public Map<String, String> getChangeNote() {
        return getPlainLiteral(SKOS.changeNote);
    }

    @Override
    public Map<String, String> getEditorialNote() {
        return getPlainLiteral(SKOS.editorialNote);
    }

    @Override
    public Map<String, String> getExample() {
        return getPlainLiteral(SKOS.example);
    }

    @Override
    public Map<String, String> getHistoryNote() {
        return getPlainLiteral(SKOS.historyNote);
    }

    @Override
    public Map<String, String> getScopeNote() {
        return getPlainLiteral(SKOS.scopeNote);
    }

    /**
     * Get a stream of all the {@link Resource}s that are related to this model with
     * the given relationship {@code type}.
     *
     * @param type       The type of relationship to search for.
     * @param transitive If transitive relationships should be searched for, rather than non-transitive relationships.
     * @return A stream of all the resources related to this {@link ConceptModel}.
     */
    public Stream<Resource> getRelationships(ConceptRelationshipType type, boolean transitive) {
        var skosProperty = type.getSkosProperty(transitive);
        var stmtIterator = resource.listProperties(skosProperty);

        return Streams.stream(stmtIterator).map(Statement::getResource);

    }
}
