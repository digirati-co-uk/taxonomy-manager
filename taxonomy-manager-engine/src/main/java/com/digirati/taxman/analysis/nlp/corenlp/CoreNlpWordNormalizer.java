package com.digirati.taxman.analysis.nlp.corenlp;

import com.digirati.taxman.analysis.nlp.AnnotationType;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.Map;
import java.util.Optional;

public interface CoreNlpWordNormalizer {
    Optional<Map<AnnotationType, String>> normalize(CoreLabel label);
}
