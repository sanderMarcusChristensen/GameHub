package com.gamehub.migration;

import com.gamehub.mapper.graph.GraphNodeMapping;
import com.gamehub.mapper.graph.GraphRelationshipMapping;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Profile("migrate-neo4j")
@RequiredArgsConstructor
public class GraphMigrationRepository {
    private final Neo4jClient neo4j;

    public void createConstraints() {
        for (GraphNodeMapping node : GraphNodeMapping.ALL) {
            String properties = Arrays.stream(node.keys().split(","))
                    .map(key -> "n." + key)
                    .collect(Collectors.joining(", "));
            String query = """
                    CREATE CONSTRAINT gamehub_%s IF NOT EXISTS
                    FOR (n:%s) REQUIRE (%s) IS UNIQUE
                    """.formatted(node.table(), node.label(), properties);
            neo4j.query(query).run();
        }
    }

    @Transactional
    public void saveAndVerify(Map<String, List<Map<String, Object>>> data) {
        migrateNodes(data);
        migrateRelationships(data);
        verifyNodes(data);
    }

    private void migrateNodes(Map<String, List<Map<String, Object>>> data) {
        for (GraphNodeMapping node : GraphNodeMapping.ALL) {
            String query = """
                    UNWIND $rows AS row
                    MERGE (n:%s {%s})
                    ON CREATE SET n = row
                    """.formatted(node.label(), node.keyPattern());
            neo4j.query(query).bind(data.get(node.table())).to("rows").run();
        }
    }

    private void migrateRelationships(Map<String, List<Map<String, Object>>> data) {
        Map<String, Long> expectedByType = new LinkedHashMap<>();
        for (GraphRelationshipMapping edge : GraphRelationshipMapping.ALL) {
            var rows = edge.rowsFor(data.get(edge.table()));
            saveAndVerifyRelationship(edge, rows);
            expectedByType.merge(edge.type(), (long) rows.size(), Long::sum);
        }
        expectedByType.forEach((type, expected) ->
                verifyCount(type, "MATCH ()-[r:" + type + "]->() RETURN count(r)", Map.of(), expected));
    }

    private void saveAndVerifyRelationship(GraphRelationshipMapping edge, List<Map<String, Object>> rows) {
        String endpoints = """
                MATCH (a:%s)
                MATCH (b:%s)
                """.formatted(edge.from(), edge.to());
        String relationship = "(a)-[r:%s%s]->(b)".formatted(edge.type(), edge.identityPattern());

        String saveQuery = """
                UNWIND $rows AS row
                %s
                MERGE %s
                """.formatted(endpoints, relationship);
        if (edge.storesRow()) {
            saveQuery += "ON CREATE SET r = row";
        }
        neo4j.query(saveQuery).bind(rows).to("rows").run();

        // Each source row must match exactly once, even when the total count is correct.
        String propertyCheck = edge.storesRow() ? "WHERE properties(r) = row" : "";
        String verificationQuery = """
                UNWIND $rows AS row
                WITH row WHERE COUNT {
                    %s
                    MATCH %s
                    %s
                } = 1
                RETURN count(*)
                """.formatted(endpoints, relationship, propertyCheck);
        verifyCount(edge.table() + " " + edge.type(), verificationQuery, Map.of("rows", rows), rows.size());
    }

    private void verifyNodes(Map<String, List<Map<String, Object>>> data) {
        for (GraphNodeMapping node : GraphNodeMapping.ALL) {
            var rows = data.get(node.table());
            String matchingNodes = """
                    UNWIND $rows AS row
                    MATCH (n:%s {%s})
                    WHERE properties(n) = row
                    RETURN count(*)
                    """.formatted(node.label(), node.keyPattern());
            verifyCount(node.table(), matchingNodes, Map.of("rows", rows), rows.size());
            verifyCount(node.label(), "MATCH (n:" + node.label() + ") RETURN count(n)", Map.of(), rows.size());
        }
    }

    private void verifyCount(String name, String query, Map<String, Object> parameters, long expected) {
        long actual = neo4j.query(query).bindAll(parameters).fetchAs(Long.class).one().orElseThrow();
        if (actual != expected) {
            throw new IllegalStateException(name + ": expected " + expected + " matches, found " + actual);
        }
    }
}
