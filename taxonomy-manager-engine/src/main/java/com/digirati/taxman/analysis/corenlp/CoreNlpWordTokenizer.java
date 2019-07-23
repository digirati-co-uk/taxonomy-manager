package com.digirati.taxman.analysis.corenlp;

import com.digirati.taxman.analysis.AnnotationType;
import com.digirati.taxman.analysis.WordToken;
import com.digirati.taxman.analysis.WordTokenizer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CoreNlpWordTokenizer implements WordTokenizer {

    private final StanfordCoreNLP nlp;

    CoreNlpWordTokenizer(StanfordCoreNLP nlp) {
        this.nlp = nlp;
    }

    public static WordTokenizer create(String language) {
        var nlp = new CoreNlpPipelineBuilder()
                .setLanguage(language)
                .addAnnotator("tokenize")
                .addAnnotator("ssplit")
                .addAnnotator("pos")
                .addAnnotator("lemma")
                .build();

        return new CoreNlpWordTokenizer(nlp);
    }

    @Override
    public List<WordToken> tokenize(String input) {
        final Annotation phrase = nlp.process(input);

        List<CoreLabel> labels = phrase.get(CoreAnnotations.TokensAnnotation.class);
        List<WordToken> tokens = new ArrayList<>(labels.size());

        for (var label : labels) {
            String lemma = label.lemma();
            String token = label.originalText();
            String pos = label.tag();
            int offset = label.beginPosition();

            // Skip punctuation tags, which are denoted by symbols in the Penn Treebank tag dictionary,
            // but keep sentence delimiters (periods, question/exclamation marks, EOL).
            if (!pos.equals(".") && pos.matches("[^a-zA-Z]+")) {
                continue;
            }

            var tokenAnnotations = Map.of(
                    AnnotationType.LEMMA, lemma,
                    AnnotationType.TOKEN, token
            );

            tokens.add(new WordToken(offset, tokenAnnotations));
        }

        return tokens;
    }
}
