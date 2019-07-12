package com.digirati.taxman.common.taxonomy;

import com.digirati.taxman.common.rdf.RdfModel;
import com.digirati.taxman.common.rdf.annotation.RdfConstructor;
import com.digirati.taxman.common.rdf.annotation.RdfContext;
import com.digirati.taxman.common.rdf.annotation.RdfType;
import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

import java.util.Map;
import java.util.stream.Stream;

@RdfType("rdfs:Dataset")
@RdfContext({"dcterms=" + DCTerms.NS})
public class ProjectModel implements RdfModel {

    private final Resource resource;

    @RdfConstructor
    public ProjectModel(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public String getSlug() {
        return getStringProperty(DCTerms.identifier);
    }

    public Map<String, String> getTitle() {
        return getPlainLiteral(DCTerms.title);
    }

    public Stream<ConceptSchemeModel> getConceptSchemes() {
        return Streams.stream(resource.listProperties(DCTerms.hasPart))
                .map(Statement::getResource)
                .map(ConceptSchemeModel::new);
    }
}
