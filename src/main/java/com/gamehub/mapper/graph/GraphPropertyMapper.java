package com.gamehub.mapper.graph;

import com.gamehub.migration.MySqlSnapshot;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Converts snapshot values to the property types stored in Neo4j. */
public final class GraphPropertyMapper {
    private GraphPropertyMapper() {}

    public static Map<String, List<Map<String, Object>>> graphProperties(MySqlSnapshot snapshot) {
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        snapshot.tables().forEach((table, rows) -> data.put(table, rows.stream()
            .map(row -> graphRow(table, row)).toList()));
        return data;
    }

    private static Map<String, Object> graphRow(String table, Map<String, Object> source) {
        Map<String, Object> row = new LinkedHashMap<>(source);
        for (var field : source.entrySet()) {
            if (field.getValue() instanceof BigInteger number) {
                try {
                    row.put(field.getKey(), number.longValueExact());
                } catch (ArithmeticException e) {
                    throw new IllegalArgumentException(table + "." + field.getKey()
                        + " exceeds Neo4j's integer range: " + number, e);
                }
            }
        }
        // Store hundredths to preserve the exact SQL decimal through a graph round trip.
        if (table.equals("player_statistics")) {
            BigDecimal rate = (BigDecimal) row.remove("win_rate");
            row.put("win_rate_hundredths", rate.movePointRight(2).longValueExact());
        }
        return row;
    }
}
