package com.digirati.taxman.rest.server;

import com.digirati.taxman.common.rdf.RdfModelFactory;
import com.digirati.taxman.common.rdf.RdfModelFormat;
import com.digirati.taxman.common.rdf.io.RdfModelReader;
import com.digirati.taxman.common.rdf.io.RdfModelWriter;
import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;
import com.digirati.taxman.rest.server.infrastructure.media.writer.TypedRdfModelMessageBodyWriter;
import com.github.jsonldjava.core.JsonLdOptions;
import com.google.common.io.Resources;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.JsonLDWriteContext;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.vocabulary.SKOS;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PreferredLabelExtractor {

    private static List<String> SOURCE_LIST = List.of(
            "https://crutaxonomy.poolparty.biz/CRUvocabularies/b4248b52-46f4-44fc-8e6e-a039f65eb8b4",
            "https://crutaxonomy.poolparty.biz/CRUvocabularies/20215cff-2f17-4a23-b75a-82dd807eef8f",
            "https://crutaxonomy.poolparty.biz/CRUvocabularies/e165d861-3ba8-4980-807b-75037e4b5f13",
            "https://crutaxonomy.poolparty.biz/CRUvocabularies/772c721d-265b-49c2-ad88-cf74d2d51220"
    );

    public static void main(String[] argv) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(Files.newInputStream(Paths.get("skos.xml")), null, "RDFXML");

        RdfModelFactory modelFactory = new RdfModelFactory();
        RdfModelReader reader = new RdfModelReader(modelFactory);
        RdfModelWriter writer = new RdfModelWriter();
        List<ConceptModel> concepts = reader.readAll(ConceptModel.class, RdfModelFormat.RDFXML, Files.newInputStream(Paths.get("skos.xml")));

        for (int i = 0; i < concepts.size(); i++) {
            ConceptModel concept = concepts.get(i);

            if (SOURCE_LIST.contains(concept.getUri().toString())) {
                var frameUrl = TypedRdfModelMessageBodyWriter.class.getClassLoader().getResource("jsonld/framing/concept.json");
                var frameString = Resources.toString(frameUrl, StandardCharsets.UTF_8);

                JSONObject frameObject = new JSONObject(frameString);
                    frameObject.put("@id", concept.getUri().toASCIIString());

                var jsonLdOptions = new JsonLdOptions();
                jsonLdOptions.setCompactArrays(true);
                jsonLdOptions.setOmitGraph(true);
                jsonLdOptions.setOmitDefault(false);
                jsonLdOptions.setUseNativeTypes(true);
                jsonLdOptions.useNamespaces = true;

                var jsonLdContext = new JsonLDWriteContext();
                jsonLdContext.setFrame(frameObject.toString());
                jsonLdContext.setOptions(jsonLdOptions);

                try (var fos = new FileOutputStream("orphan-" + i + ".json")) {
                    writer.write(concept, RdfModelFormat.JSON_LD_FRAMED, fos, jsonLdContext);
                }
            }
        }
    }
}
