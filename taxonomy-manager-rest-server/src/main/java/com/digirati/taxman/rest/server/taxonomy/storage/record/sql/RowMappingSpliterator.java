package com.digirati.taxman.rest.server.taxonomy.storage.record.sql;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A {@link Spliterator} implementation that wraps a closeable {@link ResultSet} and maps each record using a
 * {@link RowMapper} implementation.
 *
 * @param <T> The type to be mapped by a {@link RowMapper}.
 */
public class RowMappingSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

    public static <T> Stream<T> stream(RowMapper<T> mapper, ResultSet resultSet) {
        return StreamSupport
                .stream(new RowMappingSpliterator<>(mapper, resultSet), false)
                .onClose(() -> {
                    try {
                        resultSet.close();
                    } catch (Exception ex) {
                        throw new UncheckedSqlException("Unable to close database connection", ex);
                    }
                });
    }

    private int rowCount = 0;
    private final RowMapper<T> mapper;
    private final ResultSet resultSet;

    private RowMappingSpliterator(RowMapper<T> mapper, ResultSet resultSet) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.mapper = mapper;
        this.resultSet = resultSet;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        try {
            boolean hasNext = resultSet.next();
            if (hasNext) {
                consumer.accept(mapper.mapRow(resultSet, rowCount++));
            }
            return hasNext;
        } catch (SQLException ex) {
            throw new UncheckedSqlException("Unable to fetch next row", ex);
        }
    }
}
