package com.digirati.taxman.analysis.nlp.corenlp.normalizer;

import com.digirati.taxman.analysis.nlp.AnnotationType;
import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordNormalizer;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.Map;
import java.util.Optional;

public class CoreNlpDefaultWordNormalizer implements CoreNlpWordNormalizer {
    @Override
    public Optional<Map<AnnotationType, String>> normalize(CoreLabel label) {
        String lemma = label.lemma();
        String token = label.originalText();

        return Optional.of(Map.of(
                AnnotationType.LEMMA, lemma.toLowerCase(),
                AnnotationType.TOKEN, token.toLowerCase()
        ));
    }
}
