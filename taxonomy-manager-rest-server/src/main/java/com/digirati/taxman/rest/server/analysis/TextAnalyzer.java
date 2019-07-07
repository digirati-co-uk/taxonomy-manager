package com.digirati.taxman.rest.server.analysis;

import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.analysis.TextAnalysisInput;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.digirati.taxonomy.manager.lookup.TextLookupService;
import org.apache.jena.vocabulary.SKOS;

import javax.ws.rs.WebApplicationException;
import java.net.URI;

public class TextAnalyzer {
    private final TextLookupService lookupService;
    private final RdfModelFactory modelFactory;
    private final ConceptModelRepository concepts;

    public TextAnalyzer(TextLookupService lookupService, RdfModelFactory modelFactory, ConceptModelRepository concepts) {
        this.lookupService = lookupService;
        this.modelFactory = modelFactory;
        this.concepts = concepts;
    }

    /**
     * Run the auto-tagger implementation on the given {@code input} and return a collection of {@link ConceptModel}s that
     * are found in the input.
     *
     * @param input The document to tag.
     * @return A list of {@link ConceptModel}s appearing as tags.
     */
    public CollectionModel tagDocument(TextAnalysisInput input) {
        var ctx = lookupService.search(input.getText());
        var matches = ctx.getMatchedConcepts();

        try {
            var builder = modelFactory.createBuilder(CollectionModel.class);
            builder.setUri(URI.create("urn:collection"));

            var matchedConcepts = matches.parallelStream()
                    .flatMap(match -> match.getConceptIds().stream())
                    .map(concepts::find);

            matchedConcepts.forEach(concept -> {
                try {
                    builder.addEmbeddedModel(SKOS.member, concept);
                } catch (RdfModelException ex) {
                    throw new WebApplicationException("Produced invalid RDF model tagging document", ex);
                }
            });

            return builder.build();
        } catch (RdfModelException ex) {
            throw new WebApplicationException("Produced invalid RDF model tagging document", ex);
        }
    }
}
