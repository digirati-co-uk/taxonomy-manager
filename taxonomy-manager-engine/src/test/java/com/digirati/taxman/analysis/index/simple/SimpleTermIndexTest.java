package com.digirati.taxman.analysis.index.simple;

import com.digirati.taxman.analysis.corenlp.CoreNlpWordTokenizer;
import com.digirati.taxman.analysis.index.TermIndexTestSuite;
import com.digirati.taxman.analysis.index.TermIndex;
import org.junit.jupiter.api.Nested;

class SimpleTermIndexTest {

    @Nested
    class Suite extends TermIndexTestSuite {

        @Override
        protected TermIndex<String> create() {
            return new SimpleTermIndex<>(CoreNlpWordTokenizer.create("en"));
        }
    }
}
