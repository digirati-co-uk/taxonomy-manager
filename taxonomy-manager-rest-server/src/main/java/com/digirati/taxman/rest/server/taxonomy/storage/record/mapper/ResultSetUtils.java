package com.digirati.taxman.rest.server.taxonomy.storage.record.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONArray;
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
    public static Multimap<String, String> getPlainLiteralMap(ResultSet rs, String column) throws SQLException {
        Multimap<String, String> map = ArrayListMultimap.create();
        String json = rs.getString(column);

        if (json == null) {
            return map;
        }

        JSONObject jsonValue = new JSONObject(json);

        for (String key : jsonValue.keySet()) {
            Object value = jsonValue.get(key);

            if (value instanceof JSONArray) {
                JSONArray valueArray = (JSONArray) value;
                valueArray.forEach(label -> map.put(key, (String) label));
            } else if (value instanceof String) {
                map.put(key, (String) value);
            }
        }

        return map;
    }

    public static <T> List<T> getJsonArray(ResultSet rs, Class<T[]> type, String column) throws SQLException {
        String json = rs.getString(column);
        List<T> list = new ArrayList<>();

        if (json == null) {
            return list;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Arrays.asList(objectMapper.readValue(json, type));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
