package com.digirati.taxonomy.manager.normalisation;

import com.google.common.annotations.VisibleForTesting;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Provides a mechanism for normalising any text inputs, including the terms to search for and the
 * text in which to search.
 */
public class TextNormaliser {

    private final Set<String> stopwords;

    private final StanfordCoreNLP pipeline;

    private final Function<String, CoreDocument> coreDocumentGenerator;

    /**
     * Initialises a {@link TextNormaliser} for normalising any input language. Currently, full
     * lemmatisation is only supported for English due to a restriction of the NLP library that
     * we're using, but language-specific tokenisation should still work
     *
     * @param stopwords a collection of common boilerplate words to remove from the text (e.g.
     *     "the", "a", etc.)
     * @param languageKey the ISO 639-1 language code for the text to be normalised
     * @param languageName the name of the language of the text to be normalised
     */
    public static Future<TextNormaliser> initialiseNormaliser(
            Set<String> stopwords, String languageKey, String languageName) {
        return Executors.newSingleThreadExecutor()
                .submit(
                        () -> {
                            StanfordCoreNLP p = initialisePipeline(languageKey, languageName);
                            return new TextNormaliser(stopwords, p, CoreDocument::new);
                        });
    }

    private static StanfordCoreNLP initialisePipeline(String languageKey, String languageName) {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        properties.setProperty("tokenize.language", languageKey);
        properties.setProperty("language", languageName);
        return new StanfordCoreNLP(properties);
    }

    /**
     * Initialises a {@link TextNormaliser} capable of correctly lemmatising English text
     *
     * @param stopwords a collection of common boilerplate words to remove from the text (e.g.
     *     "the", "a", etc.)
     */
    public static Future<TextNormaliser> initialiseEnglishNormaliser(Set<String> stopwords) {
        return initialiseNormaliser(stopwords, "en", "English");
    }

    @VisibleForTesting
    TextNormaliser(
            Set<String> stopwords,
            StanfordCoreNLP pipeline,
            Function<String, CoreDocument> coreDocumentGenerator) {
        this.stopwords = stopwords;
        this.pipeline = pipeline;
        this.coreDocumentGenerator = coreDocumentGenerator;
    }

    /**
     * Applies the NLP pipeline to a piece of input text to produce a normalised version of it. This
     * includes tokenisation, sentence splitting, part of speech tagging, and lemmatisation (if this
     * is supported for the language in question).
     *
     * @param text a non-normalised piece of text
     * @return the normalised form of the input text
     */
    public String normalise(String text) {
        StringBuilder normalisedText = new StringBuilder();
        CoreDocument document = coreDocumentGenerator.apply(text);
        pipeline.annotate(document);
        document.tokens().stream()
                .filter(this::isContentWord)
                .forEach(coreLabel -> normalisedText.append(coreLabel.lemma()).append(" "));
        return normalisedText.toString().trim();
    }

    private boolean isContentWord(CoreLabel label) {
        return !stopwords.contains(label.originalText()) && !stopwords.contains(label.lemma());
    }
}
