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

@RdfType("http://www.w3.org/2004/02/skos/core#ConceptScheme")
@RdfContext({"skos=" + SKOS.uri, "dcterms=" + DCTerms.NS})
public class ConceptSchemeModel implements RdfModel, PersistentModel {

    private final Resource resource;
    private UUID uuid;

    @RdfConstructor
    public ConceptSchemeModel(Resource resource) {
        this(null, resource);
    }

    public ConceptSchemeModel(UUID uuid, Resource resource) {
        this.uuid = uuid;
        this.resource = resource;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public Map<String, String> getTitle() {
        return getPlainLiteral(DCTerms.title);
    }

    /**
     * Get a stream of all the {@code top-level} concepts that appear within the RDF graph of this
     * concept scheme.
     */
    public Stream<ConceptModel> getTopConcepts() {
        return Streams.stream(resource.listProperties(SKOS.hasTopConcept))
                .map(Statement::getResource)
                .map(ConceptModel::new);
    }
}
