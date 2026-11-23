package com.gamehub.migrationlogic.graphdb;

import java.sql.DriverManager;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;

import com.gamehub.migrationlogic.graphdb.graphmappers.GraphNodeMapping;
import com.gamehub.migrationlogic.graphdb.graphmappers.GraphPropertyMapper;
import com.gamehub.migrationlogic.graphdb.graphmappers.GraphRelationshipMapping;

/** Standalone command. Load GameHub configuration without starting controllers or repositories. */
public class GraphMigration {
    public static void main(String[] args) throws Exception {
        try (var context = new SpringApplicationBuilder(GraphMigration.class)
                .web(WebApplicationType.NONE).run(args)) {
            Environment config = context.getEnvironment();
            var jdbc = new java.util.Properties();
            jdbc.setProperty("user", config.getRequiredProperty("spring.datasource.username"));
            jdbc.setProperty("password", config.getRequiredProperty("spring.datasource.password"));
            jdbc.setProperty("forceConnectionTimeZoneToSession", "true");
            MySqlSnapshot snapshot;
            try (var sql = DriverManager.getConnection(config.getRequiredProperty("spring.datasource.url"), jdbc)) {
                snapshot = MySqlSnapshot.read(sql);
            }
            var data = GraphPropertyMapper.graphProperties(snapshot);
            try (var driver = GraphDatabase.driver(config.getRequiredProperty("spring.neo4j.uri"),
                    AuthTokens.basic(config.getRequiredProperty("spring.neo4j.authentication.username"),
                                     config.getRequiredProperty("spring.neo4j.authentication.password")));
                 Session session = driver.session()) {
                driver.verifyConnectivity();
                createConstraints(session);
                // Closing without commit rolls back every data write if verification fails.
                try (Transaction tx = session.beginTransaction()) {
                    migrate(tx, data);
                    tx.commit();
                }
            }
            data.forEach((table, rows) -> System.out.println(table + ": " + rows.size() + " verified rows"));
            System.out.println("Neo4j migration completed and verified.");
        }
    }

    private static void createConstraints(Session session) {
        for (GraphNodeMapping node : GraphNodeMapping.ALL) {
            String properties = Arrays.stream(node.keys().split(",")).map(key -> "n." + key)
                .collect(Collectors.joining(", "));
            session.run("CREATE CONSTRAINT gamehub_" + node.table() + " IF NOT EXISTS FOR (n:" + node.label()
                + ") REQUIRE (" + properties + ") IS UNIQUE").consume();
        }
    }

    private static void migrate(Transaction tx, Map<String, List<Map<String, Object>>> data) {
        // All endpoints must exist before relationships can be imported.
        for (GraphNodeMapping node : GraphNodeMapping.ALL) {
            tx.run("UNWIND $rows AS row MERGE (n:" + node.label() + " {" + node.keyPattern() + "}) ON CREATE SET n = row",
                Map.of("rows", data.get(node.table()))).consume();
        }
        Map<String, Long> expectedRelationships = new LinkedHashMap<>();
        for (GraphRelationshipMapping edge : GraphRelationshipMapping.ALL) {
            var rows = edge.rowsFor(data.get(edge.table()));
            writeEdge(tx, edge, rows);
            expectedRelationships.merge(edge.type(), (long) rows.size(), Long::sum);
        }
        for (var entry : expectedRelationships.entrySet()) {
            long actual = tx.run("MATCH ()-[r:" + entry.getKey() + "]->() RETURN count(r) AS count")
                .single().get("count").asLong();
            if (actual != entry.getValue()) throw new IllegalStateException(entry.getKey() + " has unexpected relationships");
        }
        for (GraphNodeMapping node : GraphNodeMapping.ALL) {
            var rows = data.get(node.table());
            checked(tx, "UNWIND $rows AS row MATCH (n:" + node.label() + " {" + node.keyPattern()
                + "}) WHERE properties(n) = row RETURN count(*) AS count", rows, node.table());
            long count = tx.run("MATCH (n:" + node.label() + ") RETURN count(n) AS count").single().get("count").asLong();
            if (count != rows.size()) throw new IllegalStateException(node.label() + " contains unexpected nodes");
        }
    }

    private static void writeEdge(Transaction tx, GraphRelationshipMapping edge, List<Map<String, Object>> rows) {
        String endpoints = "MATCH (a:" + edge.from() + ") MATCH (b:" + edge.to() + ") ";
        String pattern = "(a)-[r:" + edge.type() + edge.identityPattern() + "]->(b)";
        String write = "UNWIND $rows AS row " + endpoints + "MERGE " + pattern;
        if (edge.storesRow()) write += " ON CREATE SET r = row";
        tx.run(write, Map.of("rows", rows)).consume();

        // Count matches per source row. A duplicate must never cancel out a missing endpoint.
        String match = endpoints + "MATCH " + pattern;
        if (edge.storesRow()) match += " WHERE properties(r) = row";
        checked(tx, "UNWIND $rows AS row WITH row WHERE COUNT { " + match
            + " } = 1 RETURN count(*) AS count", rows, edge.table() + " " + edge.type());
    }

    private static void checked(Transaction tx, String query, List<Map<String, Object>> rows, String name) {
        long count = tx.run(query, Map.of("rows", rows)).single().get("count").asLong();
        if (count != rows.size()) throw new IllegalStateException(name + " verification failed (matched/expected): " + count + "/" + rows.size());
    }

}
