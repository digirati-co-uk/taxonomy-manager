package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.SemanticRelationType;
import com.fasterxml.jackson.core.JsonParseException;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SkosTranslator {

    private static final Logger logger = LogManager.getLogger(SkosTranslator.class);

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
                logger.debug(
                        () -> "Found an unsupported RDF type when deserialising RDF: " + rdfType);
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
        return new ConceptModel()
                .setId(resource.getURI())
                .setPreferredLabel(extractJson(resource, SKOS.prefLabel))
                .setAltLabel(extractJson(resource, SKOS.altLabel))
                .setHiddenLabel(extractJson(resource, SKOS.hiddenLabel))
                .setNote(extractJson(resource, SKOS.note))
                .setChangeNote(extractJson(resource, SKOS.changeNote))
                .setEditorialNote(extractJson(resource, SKOS.editorialNote))
                .setExample(extractJson(resource, SKOS.example))
                .setHistoryNote(extractJson(resource, SKOS.historyNote))
                .setScopeNote(extractJson(resource, SKOS.scopeNote));
    }

    private String extractJson(Resource resource, Property property) {
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
        return arrayNode.toString();
    }

    private ConceptSchemeModel extractConceptScheme(Resource resource) {
        ConceptSchemeModel conceptScheme = new ConceptSchemeModel();
        conceptScheme.setId(resource.getURI());
        return conceptScheme;
    }

    private List<ConceptSemanticRelationModel> extractRelationships(Model model) {
        List<ConceptSemanticRelationModel> conceptSemanticRelations = new ArrayList<>();
        for (Statement statement : model.listStatements().toList()) {
            if (statement.getObject().isLiteral()) {
                continue;
            }
            if (SKOS.broader.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.BROADER, false));
            } else if (SKOS.broaderTransitive.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.BROADER, true));
            } else if (SKOS.narrower.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.NARROWER, false));
            } else if (SKOS.narrowerTransitive.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.NARROWER, true));
            } else if (SKOS.related.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.RELATED, false));
            } else if (SKOS.inScheme.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.IN_SCHEME, false));
            } else if (SKOS.hasTopConcept.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.HAS_TOP_CONCEPT, false));
            } else if (SKOS.topConceptOf.equals(statement.getPredicate())) {
                conceptSemanticRelations.add(
                        createRelation(statement, SemanticRelationType.TOP_CONCEPT_OF, false));
            } else {
                logger.debug(
                        () ->
                                "Found an unsupported relation type when deserialising RDF: "
                                        + statement.getPredicate());
            }
        }
        return conceptSemanticRelations;
    }

    private ConceptSemanticRelationModel createRelation(
            Statement statement, SemanticRelationType semanticRelationType, boolean transitive) {
        ConceptSemanticRelationModel conceptSemanticRelation = new ConceptSemanticRelationModel();
        conceptSemanticRelation.setSourceId(statement.getSubject().getURI());
        conceptSemanticRelation.setTargetId(statement.getObject().asResource().getURI());
        conceptSemanticRelation.setRelation(semanticRelationType);
        conceptSemanticRelation.setTransitive(transitive);
        return conceptSemanticRelation;
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
                        () ->
                                getResourceNotFoundMessage(
                                        "subject", rdfModel, conceptSemanticRelation));
                continue;
            }
            Resource object = getResource(model, conceptSemanticRelation.getTargetId());
            if (object == null) {
                logger.debug(
                        () ->
                                getResourceNotFoundMessage(
                                        "object", rdfModel, conceptSemanticRelation));
                continue;
            }
            subject.addProperty(predicate, object);
            model.createStatement(subject, predicate, conceptSemanticRelation.getTargetId());
        }
        return model;
    }

    private void addConceptProperties(Resource conceptResource, Property key, String value) {
        if (value != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(value);
                Iterator<JsonNode> childNodes = rootNode.elements();
                while (childNodes.hasNext()) {
                    JsonNode propertyJson = childNodes.next();
                    conceptResource.addProperty(
                            key,
                            propertyJson.get("value").asText(),
                            propertyJson.get("language").asText());
                }
            } catch (JsonParseException e) {
                logger.warn(
                        () ->
                                "Unable to parse json - assuming a single translation. Json String: "
                                        + value,
                        e);
                conceptResource.addProperty(key, value);

            } catch (IOException e) {
                throw new IllegalStateException("Unable to parse json string: " + value, e);
            }
        }
    }

    private void relationshipToRdfStatement(
            Model model,
            ConceptSemanticRelationModel relationship,
            Resource conceptSchemeResource) {
        Resource object = getResource(model, relationship.getTargetId());
        if (object == null) {
            logger.debug(() -> "Unable to locate object for relation: " + relationship);
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
