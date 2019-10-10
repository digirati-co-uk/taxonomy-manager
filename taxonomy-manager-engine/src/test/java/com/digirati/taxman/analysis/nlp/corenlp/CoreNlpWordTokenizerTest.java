package com.digirati.taxman.analysis.nlp.corenlp;

import com.digirati.taxman.analysis.WordTokenizer;
import com.digirati.taxman.analysis.WordTokenizerTestSuite;
import org.junit.jupiter.api.Nested;

public class CoreNlpWordTokenizerTest {

    @Nested
    class English extends WordTokenizerTestSuite.English {

        @Override
        protected WordTokenizer create() {
            return CoreNlpWordTokenizer.create("en");
        }

    }
}
