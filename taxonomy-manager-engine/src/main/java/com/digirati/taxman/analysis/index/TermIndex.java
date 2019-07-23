package com.digirati.taxman.analysis.index;

import java.util.Map;
import java.util.Set;

public interface TermIndex<IdT> {

    default void addAll(Map<IdT, String> terms) {
        terms.forEach(this::add);
    }

    void add(IdT entry, String text);

    void remove(IdT entry);

    Set<IdT> search(String input);
}
