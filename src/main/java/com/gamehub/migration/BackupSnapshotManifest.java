package com.gamehub.migration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

/** Prints one comparable line per primary key, without printing the row contents. */
public class BackupSnapshotManifest {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:" + Integer.parseInt(args[0])
                + "/gameHub?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true";
        try (var db = DriverManager.getConnection(url, "root", System.getenv("MYSQL_PWD"))) {
            var tables = new TreeMap<>(MySqlSnapshot.read(db).tables());
            for (var table : tables.entrySet()) {
                String name = table.getKey();
                var keyColumns = new TreeMap<Integer, String>();
                try (var keys = db.getMetaData().getPrimaryKeys(db.getCatalog(), null, name)) {
                    while (keys.next()) {
                        keyColumns.put(keys.getInt("KEY_SEQ"), keys.getString("COLUMN_NAME"));
                    }
                }
                if (keyColumns.isEmpty()) throw new IllegalStateException("Missing primary key: " + name);
                var rows = new TreeMap<String, String>();
                for (var row : table.getValue()) {
                    var key = new StringBuilder();
                    for (String column : keyColumns.values()) {
                        appendField(key, column);
                        appendField(key, java.util.Objects.requireNonNull(row.get(column), column).toString());
                    }
                    if (rows.put(key.toString(), digest(row)) != null) {
                        throw new IllegalStateException("Duplicate key in " + name);
                    }
                }
                System.out.println(name + "\trows\t" + rows.size());
                rows.forEach((key, hash) -> System.out.println(name + "\t" + key + "\t" + hash));
            }
        }
    }

    private static String digest(Map<String, Object> row) throws NoSuchAlgorithmException {
        var encoded = new StringBuilder();
        new TreeMap<>(row).forEach((column, value) -> {
            appendField(encoded, column);
            appendField(encoded, value.toString());
        });
        var hash = MessageDigest.getInstance("SHA-256").digest(encoded.toString().getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    private static void appendField(StringBuilder output, String value) {
        output.append(value.length()).append('#').append(value);
    }
}
