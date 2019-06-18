package com.digirati.taxonomy.manager.storage.record.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public final class ResultSetUtils {
    private ResultSetUtils() {}

    public static Map<String, String> getPlainLiteralMap(ResultSet rs, String column) throws SQLException {
        JSONObject json = new JSONObject(rs.getString(column));

        Map<String, String> map = new HashMap<>();

        for (String key : json.keySet()) {
            map.put(key, json.getString(key));
        }

        return map;
    }
}
