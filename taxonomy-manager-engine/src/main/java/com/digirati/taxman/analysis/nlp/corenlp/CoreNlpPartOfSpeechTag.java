package com.digirati.taxman.analysis.nlp.corenlp;

public class CoreNlpPartOfSpeechTag {
    private final String tag;

    public  CoreNlpPartOfSpeechTag(String tag) {
        this.tag = tag;
    }

    public boolean isNoun() {
        return tag.startsWith("NN");
    }

    public boolean isPunctuation() {
        return !tag.equals(".") && tag.matches("[^a-zA-Z]+");
    }
}
