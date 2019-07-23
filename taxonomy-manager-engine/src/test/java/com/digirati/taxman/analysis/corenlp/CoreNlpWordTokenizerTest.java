package com.digirati.taxman.analysis.corenlp;

import com.digirati.taxman.analysis.EnglishWordTokenizerTestSuite;
import com.digirati.taxman.analysis.WordTokenizer;
import org.junit.jupiter.api.Nested;

public class CoreNlpWordTokenizerTest {

    @Nested
    class English extends EnglishWordTokenizerTestSuite {

        @Override
        protected WordTokenizer create() {
            return CoreNlpWordTokenizer.create("en");
        }
    }
}
