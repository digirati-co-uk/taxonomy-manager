package com.digirati.taxman.rest.server.analysis;

import com.digirati.taxman.analysis.TermMatch;
import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.analysis.TextAnalysisInput;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.SKOS;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TextAnalyzer {

    private static final Logger logger = Logger.getLogger(TextAnalyzer.class.getName());

    @Inject
    TermIndex<String, UUID> termIndex;

    @Inject
    RdfModelFactory modelFactory;

    @Inject
    ConceptModelRepository concepts;

    /**
     * Run the auto-tagger implementation on the given {@code input} and return a collection of {@link ConceptModel}s that
     * are found in the input.
     *
     * @param input The document to tag.
     * @return A list of {@link ConceptModel}s appearing as tags.
     */
    public CollectionModel tagDocument(TextAnalysisInput input) {
        String text = input.getText();
        logger.debug(text);

        var matches = input.getProjectId()
                .map(id -> termIndex.match(id, text))
                .orElseGet(() -> termIndex.match(text))
                .stream()
                .collect(Collectors.groupingBy(TermMatch::getId));

        try {
            var builder = modelFactory.createBuilder(CollectionModel.class);
            builder.setUri(URI.create("urn:collection"));

            var matchedConcepts = concepts.findAll(matches.keySet());
            matchedConcepts.forEach(concept -> {
                matches.get(concept.getUuid()).forEach(occurence ->
                    concept.getResource().addProperty(DCTerms.extent,
                            String.format(
                                    "%d:%d",
                                    occurence.getBeginPosition(),
                                    occurence.getEndPosition()
                            )
                    )
                );
                builder.addEmbeddedModel(SKOS.member, concept);
            });

            return builder.build();
        } catch (RdfModelException ex) {
            throw new WebApplicationException("Produced invalid RDF model tagging document", ex);
        }
    }
}
