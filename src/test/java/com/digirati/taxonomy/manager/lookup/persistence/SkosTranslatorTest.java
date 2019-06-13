package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkosTranslatorTest {

    private static final RdfModel conceptSchemesRdfModel = createConceptSchemesModel();

    private static final RdfModel conceptsRdfModel = createConceptsModel();

    private SkosTranslator underTest = new SkosTranslator();

    @Test
    void shouldExtractConceptSchemesFromRdf() {
        // Given
        InputStream testConceptSchemesInput = load("test-concept-schemes-input.jsonld");

        // When
        RdfModel actual =
                underTest.translate(
                        testConceptSchemesInput, "http://example.com/", SkosFileType.JSON_LD);

        // Then
        assertEquals(conceptSchemesRdfModel.getConcepts(), actual.getConcepts());
        assertEquals(conceptSchemesRdfModel.getConceptSchemes(), actual.getConceptSchemes());
        assertEquals(conceptSchemesRdfModel.getRelationships(), actual.getRelationships());
    }

    @Test
    void shouldSerialiseConceptSchemesToRdf() throws IOException {
        // When
        Model actualModel = underTest.translate(conceptSchemesRdfModel);
        StringWriter actualWriter = new StringWriter();
        actualModel.write(actualWriter, SkosFileType.JSON_LD.getFileTypeName());
        String actual = actualWriter.toString().trim();

        // Then
        InputStream expectedStream = load("test-concept-schemes-output.jsonld");
        String expected;
        try (Reader reader = new InputStreamReader(expectedStream)) {
            expected = CharStreams.toString(reader).trim();
        }

        assertEquals(expected, actual);
    }

    @Test
    void shouldExtractConceptsFromRdf() {
        // Given
        InputStream testConceptsInput = load("test-concepts-input.jsonld");

        // When
        RdfModel actual =
                underTest.translate(testConceptsInput, "http://example.com/", SkosFileType.JSON_LD);

        // Then
        assertEquals(conceptsRdfModel.getConcepts(), actual.getConcepts());
        assertEquals(conceptsRdfModel.getConceptSchemes(), actual.getConceptSchemes());
        assertEquals(conceptsRdfModel.getRelationships(), actual.getRelationships());
    }

    @Test
    void shouldSerialiseConceptsToRdf() throws IOException {
        // When
        Model actualModel = underTest.translate(conceptsRdfModel);
        StringWriter actualWriter = new StringWriter();
        actualModel.write(actualWriter, SkosFileType.JSON_LD.getFileTypeName());
        String actual = actualWriter.toString().trim();

        // Then
        InputStream expectedStream = load("test-concepts-output.jsonld");
        String expected = IOUtils.toString(expectedStream, StandardCharsets.UTF_8).trim();

        assertEquals(expected, actual);
    }

    private static RdfModel createConceptSchemesModel() {
        ConceptSchemeModel conceptSchemeModel =
                new ConceptSchemeModel("http://example.com/", "Example Scheme");

        ConceptModel one =
                new ConceptModel(
                        "http://example.com/concept#1",
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"un\"},{\"language\":\"en\",\"value\":\"one\"}]"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        ConceptModel two =
                new ConceptModel(
                        "http://example.com/concept#2",
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"deux\"},{\"language\":\"en\",\"value\":\"two\"}]"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        ConceptSemanticRelationModel schemeHasTopConceptOne =
                new ConceptSemanticRelationModel(
                        "http://example.com/",
                        "http://example.com/concept#1",
                        SemanticRelationType.HAS_TOP_CONCEPT,
                        false);
        ConceptSemanticRelationModel schemeHasTopConceptTwo =
                new ConceptSemanticRelationModel(
                        "http://example.com/",
                        "http://example.com/concept#2",
                        SemanticRelationType.HAS_TOP_CONCEPT,
                        false);

        return new RdfModel(
                Arrays.asList(one, two),
                Collections.singletonList(conceptSchemeModel),
                Arrays.asList(schemeHasTopConceptTwo, schemeHasTopConceptOne));
    }

    private static JsonNode jsonForString(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonString);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static RdfModel createConceptsModel() {
        ConceptModel one =
                new ConceptModel(
                        "http://example.com/concept#1",
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"un\"},{\"language\":\"en\",\"value\":\"one\"}]"),
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"premier\"},{\"language\":\"en\",\"value\":\"first\"}]"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        ConceptModel two =
                new ConceptModel(
                        "http://example.com/concept#2",
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"deux\"},{\"language\":\"en\",\"value\":\"two\"}]"),
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"deuxieme\"},{\"language\":\"en\",\"value\":\"second\"}]"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        ConceptModel three =
                new ConceptModel(
                        "http://example.com/concept#3",
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"trois\"},{\"language\":\"en\",\"value\":\"three\"}]"),
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"troisieme\"},{\"language\":\"en\",\"value\":\"third\"}]"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
        ConceptModel four =
                new ConceptModel(
                        "http://example.com/concept#4",
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"quatre\"},{\"language\":\"en\",\"value\":\"four\"}]"),
                        jsonForString(
                                "[{\"language\":\"fr\",\"value\":\"quatrieme\"},{\"language\":\"en\",\"value\":\"fourth\"}]"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        ConceptSemanticRelationModel oneIsNarrowerThanTwo =
                new ConceptSemanticRelationModel(
                        "http://example.com/concept#1",
                        "http://example.com/concept#2",
                        SemanticRelationType.NARROWER,
                        false);
        ConceptSemanticRelationModel twoIsRelatedToThree =
                new ConceptSemanticRelationModel(
                        "http://example.com/concept#2",
                        "http://example.com/concept#3",
                        SemanticRelationType.RELATED,
                        false);
        ConceptSemanticRelationModel twoIsRelatedToFour =
                new ConceptSemanticRelationModel(
                        "http://example.com/concept#2",
                        "http://example.com/concept#4",
                        SemanticRelationType.RELATED,
                        false);
        ConceptSemanticRelationModel threeIsBroaderThanOne =
                new ConceptSemanticRelationModel(
                        "http://example.com/concept#3",
                        "http://example.com/concept#1",
                        SemanticRelationType.BROADER,
                        false);
        ConceptSemanticRelationModel threeIsNarrowerThanFour =
                new ConceptSemanticRelationModel(
                        "http://example.com/concept#3",
                        "http://example.com/concept#4",
                        SemanticRelationType.NARROWER,
                        false);
        ConceptSemanticRelationModel fourIsBroaderThanThree =
                new ConceptSemanticRelationModel(
                        "http://example.com/concept#4",
                        "http://example.com/concept#3",
                        SemanticRelationType.BROADER,
                        false);

        return new RdfModel(
                Arrays.asList(one, three, two, four),
                new ArrayList<>(),
                Arrays.asList(
                        twoIsRelatedToFour,
                        twoIsRelatedToThree,
                        oneIsNarrowerThanTwo,
                        fourIsBroaderThanThree,
                        threeIsNarrowerThanFour,
                        threeIsBroaderThanOne));
    }

    private InputStream load(String rdfFileName) {
        return ClassLoader.getSystemResourceAsStream("skos-translator-test-data/" + rdfFileName);
    }
}
