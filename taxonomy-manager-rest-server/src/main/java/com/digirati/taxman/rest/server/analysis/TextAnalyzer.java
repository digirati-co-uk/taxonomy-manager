package com.digirati.taxman.rest.server.analysis;

import com.digirati.taxman.analysis.index.TermIndex;
import com.digirati.taxman.common.rdf.RdfModelException;
import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.taxonomy.CollectionModel;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.rest.analysis.TextAnalysisInput;
import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import org.apache.jena.vocabulary.SKOS;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import java.util.UUID;

@ApplicationScoped
public class TextAnalyzer {

    private static final Logger logger = Logger.getLogger(TextAnalyzer.class.getName());

    @Inject
    TermIndex<UUID> termIndex;

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
        logger.debug(input.getText());

        var matches = termIndex.match(input.getText());

        try {
            var builder = modelFactory.createBuilder(CollectionModel.class);
            builder.setUri(URI.create("urn:collection"));

            var matchedConcepts = concepts.findAll(matches);
            matchedConcepts.forEach(concept -> builder.addEmbeddedModel(SKOS.member, concept));

            return builder.build();
        } catch (RdfModelException ex) {
            throw new WebApplicationException("Produced invalid RDF model tagging document", ex);
        }
    }
}
