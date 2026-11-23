package com.gamehub.migrationlogic.graphdb.graphmappers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.gamehub.migrationlogic.graphdb.MySqlSnapshot;

/** Converts snapshot values to the property types stored in Neo4j. */
@Component
public class GraphPropertyMapper {

    public Map<String, List<Map<String, Object>>> mapAll(MySqlSnapshot snapshot) {
        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();
        for (var table : snapshot.tables().entrySet()) {
            String tableName = table.getKey();
            var rows = table.getValue().stream()
                    .map(row -> graphRow(tableName, row))
                    .toList();
            data.put(tableName, rows);
        }
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
