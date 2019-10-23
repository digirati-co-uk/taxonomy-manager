package com.digirati.taxman.analysis.nlp.corenlp.normalizer;

import com.digirati.taxman.analysis.nlp.AnnotationType;
import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordNormalizer;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.Map;
import java.util.Optional;

public class CoreNlpNumericalNormalizer implements CoreNlpWordNormalizer {
    @Override
    public Optional<Map<AnnotationType, String>> normalize(CoreLabel label) {
        String text = label.originalText();

        if (text.matches("^-?[0-9]+$")) {
            var normalized = text.substring(text.indexOf('-') + 1);

            return Optional.of(Map.of(AnnotationType.TOKEN, normalized));
        }

        return Optional.empty();
    }
}
