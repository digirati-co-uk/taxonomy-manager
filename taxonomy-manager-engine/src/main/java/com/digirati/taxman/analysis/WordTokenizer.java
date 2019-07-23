package com.digirati.taxman.analysis;

import java.util.List;

public interface WordTokenizer {
    List<WordToken> tokenize(String input);
}
