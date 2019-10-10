package com.digirati.taxman.analysis.nlp.corenlp.normalizer;

import com.digirati.taxman.analysis.nlp.AnnotationType;
import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpPartOfSpeechTag;
import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordNormalizer;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.Map;
import java.util.Optional;

/**
 * A normalizer that uses a simple heuristic to determine acronyms based on the original text
 * and Part of Speech tags.
 */
public class CoreNlpAcronymWordNormalizer implements CoreNlpWordNormalizer {

    @Override
    public Optional<Map<AnnotationType, String>> normalize(CoreLabel label) {
        var originalText = label.originalText();
        var pos = new CoreNlpPartOfSpeechTag(label.tag());

        if (!originalText.matches("[A-Z]+") || !pos.isNoun()) {
            return Optional.empty();
        }

        return Optional.of(Map.of(AnnotationType.TOKEN, originalText));
    }
}
