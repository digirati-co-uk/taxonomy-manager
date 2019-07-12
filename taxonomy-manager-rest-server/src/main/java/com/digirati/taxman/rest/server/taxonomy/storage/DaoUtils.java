package com.digirati.taxman.rest.server.taxonomy.storage;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;

final class DaoUtils {

    private DaoUtils() {}

    static <T> Array createArrayOf(T[] items, String itemTypeName, DataSource dataSource) {
        try (var conn = dataSource.getConnection()) {
            return conn.createArrayOf(itemTypeName, items);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
