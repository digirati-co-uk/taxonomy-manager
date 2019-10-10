package com.digirati.taxman.analysis.nlp.corenlp.normalizer;

import com.digirati.taxman.analysis.nlp.AnnotationType;
import com.digirati.taxman.analysis.nlp.corenlp.CoreNlpWordNormalizer;
import com.google.common.base.Objects;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CoreNlpStopwordNormalizer implements CoreNlpWordNormalizer {

    private static final Set<Stopword> STOPWORDS = Set.of(
            new Stopword("can", "MD"),
            new Stopword("will", "MD"),
            new Stopword("be", "VB")
    );

    @Override
    public Optional<Map<AnnotationType, String>> normalize(CoreLabel label) {
        var potentialStopword = new Stopword(label.originalText(), label.tag());

        if (STOPWORDS.contains(potentialStopword)) {
            return Optional.of(Collections.emptyMap());
        }

        return Optional.empty();
    }

    private static class Stopword {
        private final String text;
        private final String tag;

        Stopword(String text, String tag) {
            this.text = text;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Stopword stopword = (Stopword) o;
            return Objects.equal(text, stopword.text)
                    && Objects.equal(tag, stopword.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(text, tag);
        }
    }
}
