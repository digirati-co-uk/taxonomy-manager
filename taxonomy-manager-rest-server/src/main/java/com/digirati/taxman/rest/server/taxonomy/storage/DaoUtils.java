package com.digirati.taxman.rest.server.taxonomy.storage;

import com.google.common.collect.Multimap;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;

/**
 * Utility class containing helper methods for use in the DAO layer.
 */
final class DaoUtils {

    private DaoUtils() {}

    /**
     * Creates an SQL {@link Array} from an array of java objects.
     *
     * @param items the array of java objects
     * @param itemTypeName the name of the type those objects should take in the SQL query
     * @param dataSource the data source used to create the query that requires this array
     * @param <T> the java type of the items that form the array
     * @return an {@link Array} holding the given java objects
     */
    static <T> Array createArrayOf(T[] items, String itemTypeName, DataSource dataSource) {
        try (var conn = dataSource.getConnection()) {
            return conn.createArrayOf(itemTypeName, items);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static JSONObject createRdfPlainLiteral(Multimap<String, String> value) {
        var object = new JSONObject();

        value.asMap().forEach((language, labels) -> {
            object.put(language, new JSONArray(labels));
        });

        return object;
    }
}
