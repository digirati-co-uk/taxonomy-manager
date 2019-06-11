package com.digirati.taxonomy.manager.lookup.persistence;

import com.digirati.taxonomy.manager.lookup.exception.SkosPersistenceException;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSchemeModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.ConceptSemanticRelationModel;
import com.digirati.taxonomy.manager.lookup.persistence.model.RdfModel;
import org.apache.jena.rdf.model.Model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkosPersistenceService {

    private final ConceptDao conceptDao;

    private final ConceptSchemeDao conceptSchemeDao;

    private final RelationshipDao relationshipDao;

    private final SkosTranslator skosTranslator;

    public SkosPersistenceService() {
        ConnectionProvider connectionProvider = new ConnectionProvider();
        this.conceptDao = new ConceptDao(connectionProvider);
        this.conceptSchemeDao = new ConceptSchemeDao(connectionProvider);
        this.relationshipDao = new RelationshipDao(connectionProvider);
        this.skosTranslator = new SkosTranslator();
    }

    SkosPersistenceService(
            ConceptDao conceptDao,
            ConceptSchemeDao conceptSchemeDao,
            RelationshipDao relationshipDao,
            SkosTranslator skosTranslator) {
        this.conceptDao = conceptDao;
        this.conceptSchemeDao = conceptSchemeDao;
        this.relationshipDao = relationshipDao;
        this.skosTranslator = skosTranslator;
    }

    public void create(InputStream skos, String baseUrl, SkosFileType fileType)
            throws SkosPersistenceException {
        RdfModel rdfModel = skosTranslator.translate(skos, baseUrl, fileType);

        Map<String, String> idToUuid = new HashMap<>();

        for (ConceptModel concept : rdfModel.getConcepts()) {
            UUID conceptUuid = UUID.randomUUID();
            idToUuid.put(concept.getId(), conceptUuid.toString());
            concept.setId(conceptUuid.toString());
            conceptDao.create(concept);
        }

        for (ConceptSchemeModel conceptScheme : rdfModel.getConceptSchemes()) {
            UUID conceptSchemeUuid = UUID.randomUUID();
            idToUuid.put(conceptScheme.getId(), conceptScheme.toString());
            conceptScheme.setId(conceptSchemeUuid.toString());
            conceptSchemeDao.create(conceptScheme);
        }

        for (ConceptSemanticRelationModel relationship : rdfModel.getRelationships()) {
            String sourceUuid = idToUuid.get(relationship.getSourceId());
            String targetUuid = idToUuid.get(relationship.getTargetId());
            relationship.setSourceId(sourceUuid);
            relationship.setTargetId(targetUuid);
            relationshipDao.create(relationship);
        }
    }

    public OutputStream getConcept(String id, SkosFileType outputFileType)
            throws SkosPersistenceException {
        ConceptModel concept =
                conceptDao.read(id).orElseThrow(() -> SkosPersistenceException.conceptNotFound(id));

        Collection<ConceptSemanticRelationModel> relationships =
                relationshipDao.getRelationships(concept.getId());

        RdfModel rdfModel = getRelatedEntities(concept.getId(), relationships);
        rdfModel.getConcepts().add(concept);
        Model model = skosTranslator.translate(rdfModel);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        model.write(outputStream, outputFileType.getFileTypeName());
        return outputStream;
    }

    private RdfModel getRelatedEntities(
            String originalEntityId, Collection<ConceptSemanticRelationModel> relationships)
            throws SkosPersistenceException {
        Set<ConceptModel> concepts = new HashSet<>();
        Set<ConceptSchemeModel> conceptSchemes = new HashSet<>();

        for (String id : getRelatedIris(originalEntityId, relationships)) {
            Optional<ConceptModel> concept = conceptDao.read(id);
            if (concept.isPresent()) {
                concepts.add(concept.get());
            } else {
                Optional<ConceptSchemeModel> scheme = conceptSchemeDao.read(id);
                conceptSchemes.add(
                        scheme.orElseThrow(
                                () -> SkosPersistenceException.conceptSchemeNotFound(id)));
            }
        }

        return new RdfModel(concepts, conceptSchemes, relationships);
    }

    private Collection<String> getRelatedIris(
            String originalEntityIri, Collection<ConceptSemanticRelationModel> relationships) {
        return relationships.stream()
                .flatMap(r -> Stream.of(r.getSourceId(), r.getTargetId()))
                .filter(iri -> !originalEntityIri.equals(iri))
                .collect(Collectors.toSet());
    }

    public OutputStream getConceptScheme(String id, SkosFileType outputFileType)
            throws SkosPersistenceException {
        ConceptSchemeModel conceptScheme =
                conceptSchemeDao
                        .read(id)
                        .orElseThrow(() -> SkosPersistenceException.conceptSchemeNotFound(id));

        Collection<ConceptSemanticRelationModel> relationships =
                relationshipDao.getRelationships(conceptScheme.getId());

        RdfModel rdfModel = getRelatedEntities(conceptScheme.getId(), relationships);
        rdfModel.getConceptSchemes().add(conceptScheme);
        Model model = skosTranslator.translate(rdfModel);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        model.write(outputStream, outputFileType.getFileTypeName());
        return outputStream;
    }

    public void update(InputStream skos, String baseUrl, SkosFileType fileType)
            throws SkosPersistenceException {
        RdfModel rdfModel = skosTranslator.translate(skos, baseUrl, fileType);

        for (ConceptModel conceptModel : rdfModel.getConcepts()) {
            conceptDao.update(conceptModel);
        }

        for (ConceptSemanticRelationModel relationship : rdfModel.getRelationships()) {
            relationshipDao.update(relationship);
        }
    }

    public void deleteConceptScheme(String conceptSchemePrimaryKey)
            throws SkosPersistenceException {
        ConceptSchemeModel toDelete =
                conceptSchemeDao
                        .read(conceptSchemePrimaryKey)
                        .orElseThrow(
                                () ->
                                        SkosPersistenceException.conceptSchemeNotFound(
                                                conceptSchemePrimaryKey));
        relationshipDao.delete(toDelete.getId());
        conceptSchemeDao.delete(conceptSchemePrimaryKey);
    }

    public void deleteConcept(String conceptPrimaryKey) throws SkosPersistenceException {
        ConceptModel toDelete =
                conceptDao
                        .read(conceptPrimaryKey)
                        .orElseThrow(
                                () -> SkosPersistenceException.conceptNotFound(conceptPrimaryKey));
        relationshipDao.delete(toDelete.getId());
        conceptDao.delete(conceptPrimaryKey);
    }
}
