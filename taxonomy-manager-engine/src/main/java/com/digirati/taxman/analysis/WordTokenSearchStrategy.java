package com.digirati.taxman.analysis;

import java.util.List;
import java.util.Set;

/**
 *
 * @param <IdT>
 */
public interface WordTokenSearchStrategy<IdT> {
    void index(WordTokenSearchEntry<IdT> entry);

    void unindex(WordTokenSearchEntry<IdT> entry);

    Set<TermMatch<IdT>> match(List<WordToken> input);
}
