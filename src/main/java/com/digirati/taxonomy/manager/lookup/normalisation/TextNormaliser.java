package com.digirati.taxonomy.manager.lookup.normalisation;

import com.digirati.taxonomy.manager.lookup.model.Word;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides a mechanism for normalising any text inputs, including the terms to search for and the
 * text in which to search.
 */
public class TextNormaliser {

    private final Set<String> stopwords;

    private StanfordCoreNLP pipeline;

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
    public static TextNormaliser initialiseNormaliser(
            Set<String> stopwords, String languageKey, String languageName) {
        TextNormaliser normaliser = new TextNormaliser(stopwords);
        normaliser.initialisePipeline(languageKey, languageName);
        return normaliser;
    }

    /**
     * Initialises a {@link TextNormaliser} capable of correctly lemmatising English text.
     *
     * @param stopwords a collection of common boilerplate words to remove from the text (e.g.
     *     "the", "a", etc.)
     */
    public static TextNormaliser initialiseEnglishNormaliser(Set<String> stopwords) {
        return initialiseNormaliser(stopwords, "en", "English");
    }

    private TextNormaliser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    private void initialisePipeline(String languageKey, String languageName) {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        properties.setProperty("tokenize.language", languageKey);
        properties.setProperty("language", languageName);
        this.pipeline = new StanfordCoreNLP(properties);
    }

    /**
     * Applies the NLP pipeline to a piece of input text to produce a list of the {@link
     * Word}s it contains (i.e. all words, excluding stopwords). The NLP pipeline includes
     * tokenisation, sentence splitting, part of speech tagging, and lemmatisation (if this is
     * supported for the language in question).
     *
     * @param text a piece of text from which to extract the content words
     * @return the list of content words contained in the input text
     */
    public List<Word> extractContentWords(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        return document.tokens().stream()
                .filter(this::isContentWord)
                .map(Word::new)
                .collect(Collectors.toList());
    }

    private boolean isContentWord(CoreLabel label) {
        return !stopwords.contains(label.originalText()) && !stopwords.contains(label.lemma());
    }

    /**
     * Converts a list of {@link Word}s into a single normalised string.
     *
     * @param contentWords the {@link Word}s to convert
     * @return a single normalised string built from the input {@link Word}s
     */
    public String normalise(List<Word> contentWords) {
        StringBuilder normalisedText = new StringBuilder();
        contentWords.forEach(coreLabel -> normalisedText.append(coreLabel.getLemma()).append(" "));
        return normalisedText.toString().trim();
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
        return normalise(extractContentWords(text));
    }
}
