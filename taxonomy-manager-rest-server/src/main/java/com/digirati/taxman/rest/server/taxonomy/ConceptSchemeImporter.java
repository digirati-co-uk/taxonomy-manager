package com.digirati.taxman.rest.server.taxonomy;

import com.digirati.taxman.common.taxonomy.ConceptModel;
import com.digirati.taxman.common.taxonomy.ConceptSchemeModel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ConceptSchemeImporter {

    @Inject
    ConceptModelRepository conceptRepository;

    @Inject
    ConceptSchemeModelRepository conceptSchemeRepository;

    /**
     * Import the entire dataset associated with a {@link ConceptSchemeModel} into the system.
     *
     * @param model A concept scheme model, including any nested concepts or relations.
     * @return The transformed concept scheme after import into the system.
     */
    public ConceptSchemeModel importScheme(ConceptSchemeModel model, String projectSlug) {
        var scheme = conceptSchemeRepository.create(model, projectSlug);

        var conceptModels = model.getAllResources(ConceptModel.class);
        conceptModels.forEach(concept -> {
            concept.setUuid(UUID.randomUUID());
            conceptRepository.create(concept,scheme.getUuid(),projectSlug);
        });

        return conceptSchemeRepository.find(scheme.getUuid()).orElseThrow();
    }
}
