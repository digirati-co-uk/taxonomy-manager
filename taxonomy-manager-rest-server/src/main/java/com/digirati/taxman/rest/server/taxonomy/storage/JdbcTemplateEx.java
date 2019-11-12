package com.digirati.taxman.rest.server.taxonomy.storage;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * An extension of JdbcTemplate that provides a queryForOptional, as a variant
 * of queryForObject that doesn't throw if the queried object is not found.
 */
public class JdbcTemplateEx extends JdbcTemplate {

    JdbcTemplateEx(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Return an optional with either single result object from
     * the given Collection, or Optional.empty() if collection
     * is empty.
     * <p>Throws an exception if more than 1 element found.
     *
     * @param results the result Collection (can be {@code null}
     *                and is also expected to contain {@code null} elements)
     * @return the single result object
     * @throws IncorrectResultSizeDataAccessException if more than one
     *                                                element has been found in the given Collection
     */
    private static <T> Optional<T> singleOptionalResult(@Nullable Collection<T> results) throws IncorrectResultSizeDataAccessException {
        // This is identical to the nullableSingleResult implementation but returns
        // Optional.empty() instead of throwing, if the results are empty
        if (CollectionUtils.isEmpty(results)) {
            return Optional.empty();
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return Optional.of(results.iterator().next());
    }

    @NonNull
    <T> Optional<T> queryForOptional(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper)
            throws DataAccessException {

        List<T> results = query(sql, args, argTypes, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return singleOptionalResult(results);
    }

    @NonNull
    private <T> Optional<T> queryForOptional(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return singleOptionalResult(results);
    }

    @NonNull
    public <T> Optional<T> queryForOptional(String sql, RowMapper<T> rowMapper, @Nullable Object... args) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1));
        return singleOptionalResult(results);
    }

    @NonNull
    public <T> Optional<T> queryForOptional(String sql, Object[] args, int[] argTypes, Class<T> requiredType)
            throws DataAccessException {

        return queryForOptional(sql, args, argTypes, getSingleColumnRowMapper(requiredType));
    }

    @NonNull
    public <T> Optional<T> queryForOptional(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        return queryForOptional(sql, args, getSingleColumnRowMapper(requiredType));
    }

    @NonNull
    public <T> Optional<T> queryForOptional(String sql, Class<T> requiredType, @Nullable Object... args) throws DataAccessException {
        return queryForOptional(sql, args, getSingleColumnRowMapper(requiredType));
    }
}
