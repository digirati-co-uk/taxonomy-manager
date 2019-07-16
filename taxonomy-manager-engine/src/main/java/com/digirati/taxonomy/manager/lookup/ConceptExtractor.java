package com.digirati.taxonomy.manager.lookup;

import com.digirati.taxonomy.manager.lookup.model.ConceptMatch;
import com.digirati.taxonomy.manager.lookup.model.TermMatch;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Encapsulates all operations related to determining which concepts exist within a given piece of
 * text.
 */
class ConceptExtractor {

    private TextSearcher textSearcher;

    private final Multimap<String, UUID> conceptLookupTable;

    public ConceptExtractor(TextSearcher textSearcher, Multimap<String, UUID> conceptLookupTable) {
        this.textSearcher = textSearcher;
        this.conceptLookupTable = conceptLookupTable;
    }

    public Multimap<String, UUID> getConceptLookupTable() {
        return conceptLookupTable;
    }

    /**
     * Uses the {@link TextSearcher} to locate any matching terms in the input text, and retrieves
     * the IDs of any Concepts that these terms could represent.
     *
     * @param inputText the text from which to extract concepts
     * @return a collection of {@link ConceptMatch}es containing details of what terms were found,
     *     where they were found, and what concepts these terms could be representing
     */
    public Collection<ConceptMatch> extract(String inputText) {
        return textSearcher
                .search(inputText)
                .map(this::toConceptMatch)
                .collect(Collectors.toList());
    }

    private ConceptMatch toConceptMatch(TermMatch termMatch) {
        return new ConceptMatch(termMatch, conceptLookupTable.get(termMatch.getTerm()));
    }

    private Set<String> getTerms() {
        return Sets.newHashSet(conceptLookupTable.keySet());
    }

    /**
     * Adds the details of a concept to the lookup table, and reconstructs the text searcher with
     * the new terms to search for if the concept labels are not already loaded into it.
     *
     * @param conceptUuid the UUID of the concept
     * @param labels the labels of the concept
     */
    public void addConcept(UUID conceptUuid, Set<String> labels) {
        Set<String> originalTerms = getTerms();
        for (String label : labels) {
            conceptLookupTable.put(label, conceptUuid);
        }

        if (!originalTerms.containsAll(labels)) {
            textSearcher = textSearcher.rebuild(getTerms());
        }
    }

    public void addConcepts(Multimap<UUID, String> conceptUuidToLabels) {
        for (Map.Entry<UUID, String> entry : conceptUuidToLabels.entries()) {
            conceptLookupTable.put(entry.getValue(), entry.getKey());
        }
        textSearcher = textSearcher.rebuild(getTerms());
    }

    /**
     * Updates the details of a concept in the lookup table and reconstructs the text searcher with
     * the updated terms.
     *
     * @param conceptUuid the UUID of the concept
     * @param updatedLabels the labels of the updated concept
     */
    public void updateConcept(UUID conceptUuid, Set<String> updatedLabels) {
        Set<String> originalLabels =
                conceptLookupTable.entries().stream()
                        .filter(entry -> conceptUuid.equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

        if (originalLabels.equals(updatedLabels)) {
            // If nothing has changed or the preferred and alt labels have simply been swapped, we
            // don't need to change the lookup table or text searcher, as neither the lookup table
            // or text searcher care whether a label is preferred or not.
            return;
        }

        for (String label : originalLabels) {
            Set<UUID> uuids = Sets.newHashSet(conceptLookupTable.get(label));
            uuids.remove(conceptUuid);
            conceptLookupTable.replaceValues(label, uuids);
        }

        for (String label : updatedLabels) {
            conceptLookupTable.put(label, conceptUuid);
        }

        textSearcher = textSearcher.rebuild(getTerms());
    }

    /**
     * Removes the details of a concept from the lookup table and rebuilds the text searcher if the
     * last occurrence of a label was removed.
     *
     * @param conceptUuid the UUID of the concept
     * @param labels the labels of the concept
     */
    public void removeConcept(UUID conceptUuid, Set<String> labels) {
        boolean hasRemovedLastLabelInstance = false;
        for (String label : labels) {
            Collection<UUID> conceptsWithLabel = Sets.newHashSet(conceptLookupTable.get(label));
            conceptsWithLabel.remove(conceptUuid);
            conceptLookupTable.replaceValues(label, conceptsWithLabel);
            if (conceptsWithLabel.isEmpty()) {
                hasRemovedLastLabelInstance = true;
            }
        }

        if (hasRemovedLastLabelInstance) {
            textSearcher = textSearcher.rebuild(getTerms());
        }
    }
}
