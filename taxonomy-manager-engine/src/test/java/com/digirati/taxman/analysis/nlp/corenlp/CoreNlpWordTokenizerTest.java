package com.digirati.taxman.analysis.nlp.corenlp;

import com.digirati.taxman.analysis.WordToken;
import com.digirati.taxman.analysis.WordTokenizerTestSuite;
import com.digirati.taxman.analysis.WordTokenizer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoreNlpWordTokenizerTest {

    @Nested
    class English extends WordTokenizerTestSuite.English {

        @Override
        protected WordTokenizer create() {
            return CoreNlpWordTokenizer.create("en");
        }

    }
}
