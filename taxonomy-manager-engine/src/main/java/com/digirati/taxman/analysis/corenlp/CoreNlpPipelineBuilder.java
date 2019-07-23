package com.digirati.taxman.analysis.corenlp;

import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CoreNlpPipelineBuilder {

    private final Map<String, Class<? extends Annotator>> customAnnotators = new HashMap<>();
    private final List<String> annotators = new ArrayList<>();

    private String language = "en";

    public CoreNlpPipelineBuilder addCustomAnnotator(Class<? extends Annotator> type, String name) {
        customAnnotators.put(name, type);
        annotators.add(name);
        return this;
    }

    public CoreNlpPipelineBuilder addAnnotator(String name) {
        annotators.add(name);
        return this;
    }

    public CoreNlpPipelineBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    public StanfordCoreNLP build() {
        Properties props = new Properties();
        props.put("annotators", String.join(",", annotators));

        customAnnotators.forEach((name, type) -> {
            props.put("customAnnotatorClass." + name, type.getName());
        });

        props.put("tokenize.options", "splitHyphenated=true");
        props.put("tokenize.language", language);
        props.put("language", language);

        return new StanfordCoreNLP(props);
    }
}
