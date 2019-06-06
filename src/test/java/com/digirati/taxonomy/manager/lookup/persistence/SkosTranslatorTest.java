package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        RdfModel actual = underTest.translate(testConceptSchemesInput, null, SkosFileType.JSON_LD);

        // Then
        assertEquals(conceptSchemesRdfModel, actual);
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
        String expected = IOUtils.toString(expectedStream, StandardCharsets.UTF_8).trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldExtractConceptsFromRdf() {
        // Given
        InputStream testConceptsInput = load("test-concepts-input.jsonld");

        // When
        RdfModel actual = underTest.translate(testConceptsInput, null, SkosFileType.JSON_LD);

        // Then
        assertEquals(conceptsRdfModel, actual);
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
                new ConceptSchemeModel().setIri("http://example.com/");

        ConceptModel one =
                new ConceptModel()
                        .setIri("http://example.com/concept#1")
                        .setPreferredLabel(
                                "[{\"language\":\"fr\",\"value\":\"un\"},{\"language\":\"en\",\"value\":\"one\"}]");
        ConceptModel two =
                new ConceptModel()
                        .setIri("http://example.com/concept#2")
                        .setPreferredLabel(
                                "[{\"language\":\"fr\",\"value\":\"deux\"},{\"language\":\"en\",\"value\":\"two\"}]");

        ConceptSemanticRelationModel schemeHasTopConceptOne =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/")
                        .setTargetId("http://example.com/concept#1")
                        .setRelation(SemanticRelationType.HAS_TOP_CONCEPT)
                        .setTransitive(false);
        ConceptSemanticRelationModel schemeHasTopConceptTwo =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/")
                        .setTargetId("http://example.com/concept#2")
                        .setRelation(SemanticRelationType.HAS_TOP_CONCEPT)
                        .setTransitive(false);

        return new RdfModel(
                Arrays.asList(one, two),
                Collections.singletonList(conceptSchemeModel),
                Arrays.asList(schemeHasTopConceptTwo, schemeHasTopConceptOne));
    }

    private static RdfModel createConceptsModel() {
        ConceptModel one =
                new ConceptModel()
                        .setIri("http://example.com/concept#1")
                        .setPreferredLabel(
                                "[{\"language\":\"fr\",\"value\":\"un\"},{\"language\":\"en\",\"value\":\"one\"}]")
                        .setAltLabel(
                                "[{\"language\":\"fr\",\"value\":\"premier\"},{\"language\":\"en\",\"value\":\"first\"}]");
        ConceptModel two =
                new ConceptModel()
                        .setIri("http://example.com/concept#2")
                        .setPreferredLabel(
                                "[{\"language\":\"fr\",\"value\":\"deux\"},{\"language\":\"en\",\"value\":\"two\"}]")
                        .setAltLabel(
                                "[{\"language\":\"fr\",\"value\":\"deuxieme\"},{\"language\":\"en\",\"value\":\"second\"}]");
        ConceptModel three =
                new ConceptModel()
                        .setIri("http://example.com/concept#3")
                        .setPreferredLabel(
                                "[{\"language\":\"fr\",\"value\":\"trois\"},{\"language\":\"en\",\"value\":\"three\"}]")
                        .setAltLabel(
                                "[{\"language\":\"fr\",\"value\":\"troisieme\"},{\"language\":\"en\",\"value\":\"third\"}]");
        ConceptModel four =
                new ConceptModel()
                        .setIri("http://example.com/concept#4")
                        .setPreferredLabel(
                                "[{\"language\":\"fr\",\"value\":\"quatre\"},{\"language\":\"en\",\"value\":\"four\"}]")
                        .setAltLabel(
                                "[{\"language\":\"fr\",\"value\":\"quatrieme\"},{\"language\":\"en\",\"value\":\"fourth\"}]");

        ConceptSemanticRelationModel oneIsNarrowerThanTwo =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/concept#1")
                        .setTargetId("http://example.com/concept#2")
                        .setRelation(SemanticRelationType.NARROWER)
                        .setTransitive(false);
        ConceptSemanticRelationModel twoIsRelatedToThree =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/concept#2")
                        .setTargetId("http://example.com/concept#3")
                        .setRelation(SemanticRelationType.RELATED)
                        .setTransitive(false);
        ConceptSemanticRelationModel twoIsRelatedToFour =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/concept#2")
                        .setTargetId("http://example.com/concept#4")
                        .setRelation(SemanticRelationType.RELATED)
                        .setTransitive(false);
        ConceptSemanticRelationModel threeIsBroaderThanOne =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/concept#3")
                        .setTargetId("http://example.com/concept#1")
                        .setRelation(SemanticRelationType.BROADER)
                        .setTransitive(false);
        ConceptSemanticRelationModel threeIsNarrowerThanFour =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/concept#3")
                        .setTargetId("http://example.com/concept#4")
                        .setRelation(SemanticRelationType.NARROWER)
                        .setTransitive(false);
        ConceptSemanticRelationModel fourIsBroaderThanThree =
                new ConceptSemanticRelationModel()
                        .setSourceId("http://example.com/concept#4")
                        .setTargetId("http://example.com/concept#3")
                        .setRelation(SemanticRelationType.BROADER)
                        .setTransitive(false);

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
        return FileManager.get().open("skos-translator-test-data/" + rdfFileName);
    }
}
