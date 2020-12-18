package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.RdfModelContext;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Models the RDF representation of a project.
 */
@RdfType("http://www.w3.org/2000/01/rdf-schema#Dataset")
@RdfContext({"dcterms=" + DCTerms.NS, "rdfs="+ RDFS.uri})
public class ProjectModel implements RdfModel {

    private final RdfModelContext context;

    @RdfConstructor
    public ProjectModel(RdfModelContext context) {
        this.context = context;
    }

    public RdfModelContext getContext() {
        return context;
    }

    public String getSlug() {
        return getStringProperty(DCTerms.identifier);
    }

    public Multimap<String, String> getTitle() {
        return getPlainLiteral(DCTerms.title);
    }

    /**
     * Gets a stream of all {@link ConceptSchemeModel}s associated with this project.
     *
     * @return the concept schemes associated with this project
     */
    public Stream<ConceptSchemeModel> getConceptSchemes() {
        return getResources(ConceptSchemeModel.class, DCTerms.hasPart);
    }
}
