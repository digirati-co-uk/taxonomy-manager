package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SkosTranslator {

    private static final Logger logger = LogManager.getLogger(SkosTranslator.class);

    private static final Property dcTermsTitle =
            ModelFactory.createDefaultModel()
                    .createProperty("http://purl.org/dc/terms/1.1/", "title");

    SkosTranslator() {
        // no-op
    }

    public RdfModel translate(InputStream skos, String baseUrl, SkosFileType skosFileType) {
        Model model = read(skos, baseUrl, skosFileType);
        List<ConceptModel> concepts = new ArrayList<>();
        List<ConceptSchemeModel> conceptSchemes = new ArrayList<>();
        List<ConceptSemanticRelationModel> conceptSemanticRelations = extractRelationships(model);
        for (RDFNode rdfNode : model.listSubjects().toList()) {
            if (!rdfNode.isResource()) {
                continue;
            }
            Resource resource = rdfNode.asResource();
            Statement rdfType = resource.getProperty(RDF.type);
            String rdfTypeUri = rdfType == null ? null : rdfType.getObject().asResource().getURI();
            if (SKOS.Concept.getURI().equals(rdfTypeUri)) {
                concepts.add(extractConcept(resource));
            } else if (SKOS.ConceptScheme.getURI().equals(rdfTypeUri)) {
                conceptSchemes.add(extractConceptScheme(resource));
            } else {
                logger.debug("Found an unsupported RDF type when deserialising RDF: " + rdfType);
            }
        }
        return new RdfModel(concepts, conceptSchemes, conceptSemanticRelations);
    }

    private Model read(InputStream rdf, String baseUrl, SkosFileType skosFileType) {
        Model model = ModelFactory.createDefaultModel();
        model.read(rdf, baseUrl, skosFileType.getFileTypeName());
        return model;
    }

    private ConceptModel extractConcept(Resource resource) {
        return new ConceptModel(
                resource.getURI(),
                extractJson(resource, SKOS.prefLabel),
                extractJson(resource, SKOS.altLabel),
                extractJson(resource, SKOS.hiddenLabel),
                extractJson(resource, SKOS.note),
                extractJson(resource, SKOS.changeNote),
                extractJson(resource, SKOS.editorialNote),
                extractJson(resource, SKOS.example),
                extractJson(resource, SKOS.historyNote),
                extractJson(resource, SKOS.scopeNote));
    }

    private JsonNode extractJson(Resource resource, Property property) {
        List<Statement> propertyStatements = resource.listProperties(property).toList();
        if (propertyStatements == null || propertyStatements.isEmpty()) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (Statement statement : propertyStatements) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("language", statement.getLanguage());
            objectNode.put("value", statement.getString());
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

    private ConceptSchemeModel extractConceptScheme(Resource resource) {
        return new ConceptSchemeModel(
                resource.getURI(), resource.getProperty(dcTermsTitle).getString());
    }

    private List<ConceptSemanticRelationModel> extractRelationships(Model model) {
        List<ConceptSemanticRelationModel> conceptSemanticRelations = new ArrayList<>();
        for (Statement statement : model.listStatements().toList()) {
            if (statement.getObject().isLiteral()
                    || !SemanticRelationType.isMappableRdfProperty(statement.getPredicate())) {
                continue;
            }

            String sourceId = statement.getSubject().getURI();
            String targetId = statement.getObject().asResource().getURI();
            ConceptSemanticRelationModel relationship =
                    SemanticRelationType.getRelationshipGenerator(statement.getPredicate())
                            .generate(sourceId, targetId);
            conceptSemanticRelations.add(relationship);
        }
        return conceptSemanticRelations;
    }

    public Model translate(RdfModel rdfModel) {
        Model model = ModelFactory.createDefaultModel();
        for (ConceptModel concept : rdfModel.getConcepts()) {
            Resource conceptResource = model.createResource(concept.getId(), SKOS.Concept);
            addConceptProperties(conceptResource, SKOS.prefLabel, concept.getPreferredLabel());
            addConceptProperties(conceptResource, SKOS.altLabel, concept.getAltLabel());
            addConceptProperties(conceptResource, SKOS.hiddenLabel, concept.getHiddenLabel());
            addConceptProperties(conceptResource, SKOS.note, concept.getNote());
            addConceptProperties(conceptResource, SKOS.changeNote, concept.getChangeNote());
            addConceptProperties(conceptResource, SKOS.editorialNote, concept.getEditorialNote());
            addConceptProperties(conceptResource, SKOS.example, concept.getExample());
            addConceptProperties(conceptResource, SKOS.historyNote, concept.getHistoryNote());
            addConceptProperties(conceptResource, SKOS.scopeNote, concept.getScopeNote());
        }

        for (ConceptSchemeModel conceptScheme : rdfModel.getConceptSchemes()) {
            Resource conceptSchemeResource =
                    model.createResource(conceptScheme.getId(), SKOS.ConceptScheme);
            conceptSchemeResource.addProperty(dcTermsTitle, conceptScheme.getTitle());
            rdfModel.getRelationships().stream()
                    .filter(
                            relationship ->
                                    conceptScheme.getId().equals(relationship.getSourceId()))
                    .forEach(
                            relationship ->
                                    relationshipToRdfStatement(
                                            model, relationship, conceptSchemeResource));
        }

        for (ConceptSemanticRelationModel conceptSemanticRelation : rdfModel.getRelationships()) {
            Property predicate = conceptSemanticRelation.getRelationPredicate();
            Resource subject = getResource(model, conceptSemanticRelation.getSourceId());
            if (subject == null) {
                logger.debug(
                        getResourceNotFoundMessage("subject", rdfModel, conceptSemanticRelation));
                continue;
            }
            Resource object = getResource(model, conceptSemanticRelation.getTargetId());
            if (object == null) {
                logger.debug(
                        getResourceNotFoundMessage("object", rdfModel, conceptSemanticRelation));
                continue;
            }
            subject.addProperty(predicate, object);
            model.createStatement(subject, predicate, conceptSemanticRelation.getTargetId());
        }
        return model;
    }

    private void addConceptProperties(Resource conceptResource, Property key, JsonNode value) {
        if (value != null) {
            Iterator<JsonNode> childNodes = value.elements();
            while (childNodes.hasNext()) {
                JsonNode propertyJson = childNodes.next();
                conceptResource.addProperty(
                        key,
                        propertyJson.get("value").asText(),
                        propertyJson.get("language").asText());
            }
        }
    }

    private void relationshipToRdfStatement(
            Model model,
            ConceptSemanticRelationModel relationship,
            Resource conceptSchemeResource) {
        Resource object = getResource(model, relationship.getTargetId());
        if (object == null) {
            logger.debug("Unable to locate object for relation: " + relationship);
        } else {
            conceptSchemeResource.addProperty(relationship.getRelationPredicate(), object);
            model.createStatement(
                    conceptSchemeResource, relationship.getRelationPredicate(), object);
        }
    }

    private Resource getResource(Model model, String id) {
        for (RDFNode r : model.listSubjects().toList()) {
            if (r.isResource() && r.asResource().getURI().equals(id)) {
                return r.asResource();
            }
        }
        return null;
    }

    private String getResourceNotFoundMessage(
            String type, RdfModel rdfModel, ConceptSemanticRelationModel conceptSemanticRelation) {
        String baseWarning =
                "Unable to locate " + type + " for relation: " + conceptSemanticRelation;
        if (rdfModel.getConcepts().stream()
                .anyMatch(
                        conceptModel ->
                                conceptModel
                                        .getId()
                                        .equals(conceptSemanticRelation.getTargetId()))) {
            return baseWarning + " (match found in concept list)";
        }
        return baseWarning + " (not found in concept list either)";
    }
}
