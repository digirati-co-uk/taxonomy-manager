package com.digirati.taxman.rest.server.taxonomy.storage.record.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

final class ResultSetUtils {
    private ResultSetUtils() {}

    /**
     * Read a key-value map of strings from a JSON column in a {@link ResultSet}.
     *
     * @param rs The {@code ResultSet} to read from.
     * @param column The name of the column to read.
     * @return a map of <code>language</code> to <code>value</code>.
     *
     * @throws SQLException if the column had the wrong type or was not present in the {@code ResultSet}.
     */
    public static Map<String, String> getPlainLiteralMap(ResultSet rs, String column) throws SQLException {
        JSONObject json = new JSONObject(rs.getString(column));

        Map<String, String> map = new HashMap<>();

        for (String key : json.keySet()) {
            map.put(key, json.getString(key));
        }

        return map;
    }
}
