package com.digirati.taxman.analysis.nlp.corenlp;

import com.digirati.taxman.analysis.WordToken;
import com.digirati.taxman.analysis.WordTokenizer;
import com.digirati.taxman.analysis.nlp.corenlp.normalizer.CoreNlpAcronymWordNormalizer;
import com.digirati.taxman.analysis.nlp.corenlp.normalizer.CoreNlpDefaultWordNormalizer;
import com.digirati.taxman.analysis.nlp.corenlp.normalizer.CoreNlpNumericalNormalizer;
import com.digirati.taxman.analysis.nlp.corenlp.normalizer.CoreNlpStopwordNormalizer;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class CoreNlpWordTokenizer implements WordTokenizer {
    private static final List<CoreNlpWordNormalizer> DEFAULT_NORMALIZERS = List.of(
            new CoreNlpStopwordNormalizer(),
            new CoreNlpNumericalNormalizer(),
            new CoreNlpAcronymWordNormalizer(),
            new CoreNlpDefaultWordNormalizer()
    );

    private final StanfordCoreNLP nlp;
    private final List<CoreNlpWordNormalizer> normalizers;

    private final Pattern XML_TAG_REGEX = Pattern.compile("\\<\\s*\\/?\\s*\\w+\\s*\\>");

    CoreNlpWordTokenizer(StanfordCoreNLP nlp, List<CoreNlpWordNormalizer> normalizers) {
        this.nlp = nlp;
        this.normalizers = normalizers;
    }

    CoreNlpWordTokenizer(StanfordCoreNLP nlp) {
        this(nlp, DEFAULT_NORMALIZERS);
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
        // Strip HTML tags and replace them with equivalent amount of '_' chars
        input = XML_TAG_REGEX.matcher(input).replaceAll(
                matchResult -> StringUtils.repeat('_', matchResult.end() - matchResult.start()));

        // Some input strings like to delimit text with a forward slash. This causes problems for Stanford CoreNLP,
        // which expects words to be delimited by whitespace or regular punctuation.
        final String normalizedInput = input.replaceAll("/", " ");
        final Annotation phrase = nlp.process(normalizedInput);

        List<CoreLabel> labels = phrase.get(CoreAnnotations.TokensAnnotation.class);
        List<WordToken> tokens = new ArrayList<>(labels.size());

        for (var label : labels) {
            var pos = new CoreNlpPartOfSpeechTag(label.tag());
            var token = label.originalText();

            // Skip punctuation tags, which are denoted by symbols in the Penn Treebank tag dictionary,
            // but keep sentence delimiters (periods, question/exclamation marks, EOL).
            if (pos.isPunctuation() || token.matches("^[^&-.a-zA-Z0-9]+$")) {
                continue;
            }

            var annotations = normalizers.stream()
                    .flatMap(normalizer -> normalizer.normalize(label).stream())
                    .findFirst()
                    .orElseGet(Collections::emptyMap);

            // The token was elided, don't include it in the output.
            if (annotations.isEmpty()) {
                continue;
            }

            tokens.add(new WordToken(annotations, label.beginPosition(), label.endPosition()));
        }

        return tokens;
    }
}
