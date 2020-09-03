package com.digirati.taxman.analysis.search;


import com.digirati.taxman.analysis.WordToken;
import com.digirati.taxman.analysis.WordTokenSearchEntry;
import com.digirati.taxman.analysis.WordTokenSearchStrategy;
import com.digirati.taxman.analysis.nlp.AnnotationType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@State(Scope.Benchmark)
public class WordTokenSearchStrategyBenchmarks {

    @Param({"/com/digirati/taxman/analysis/search/corpus--10k-words.txt"})
    private String corpusPath;

    @Param({"/com/digirati/taxman/analysis/search/input--lorem-ipsum.txt"})
    private String inputPath;

    @Param({"com.digirati.taxman.analysis.search.NaiveSearchStrategy"})
    private String strategyClass;

    private List<WordToken> inputTokens = new ArrayList<>();

    private WordTokenSearchStrategy<Integer> strategy;

    private static List<WordToken> naiveTokenize(String line) {
        return Arrays.stream(line.split(" "))
                .map(word -> new WordToken(Map.of(AnnotationType.TOKEN, word), 1,2))
                .collect(Collectors.toList());
    }

    @Setup
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.strategy = (WordTokenSearchStrategy<Integer>) Class.forName(strategyClass).newInstance();
        this.inputTokens.clear();

        try (var inputIs = WordTokenSearchStrategyBenchmarks.class.getResourceAsStream(inputPath);
             var reader = new BufferedReader(new InputStreamReader(inputIs));
             var lines = reader.lines()) {

            lines.forEach(line -> {
                inputTokens.addAll(naiveTokenize(line));
            });
        }

        try (var corpusInput = WordTokenSearchStrategyBenchmarks.class.getResourceAsStream(corpusPath);
             var reader = new BufferedReader(new InputStreamReader(corpusInput));
             var lines = reader.lines()) {

            final int[] index = new int[1];

            lines.forEach(line -> {
                List<WordToken> tokens = naiveTokenize(line);
                strategy.index(new WordTokenSearchEntry<>(index[0]++, tokens));
            });
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(time = 5, iterations = 3)
    @Measurement(time = 5, iterations = 2)
    @Fork(2)
    public void search(Blackhole blackhole) {
        blackhole.consume(strategy.match(inputTokens));
    }

}
