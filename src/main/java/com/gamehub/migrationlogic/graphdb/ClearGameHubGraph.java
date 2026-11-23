package com.gamehub.migrationlogic.graphdb;

import java.util.Arrays;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.gamehub.migrationlogic.graphdb.graphmappers.GraphNodeMapping;
import com.gamehub.migrationlogic.graphdb.graphmappers.GraphRelationshipMapping;

/** Preview or clear the mapped GameHub graph in the migration's Neo4j database. */
public class ClearGameHubGraph {
    private static final String NODES =
            "MATCH (n) WHERE any(label IN labels(n) WHERE label IN $labels) ";
    private static final String RELATIONSHIPS = """
            MATCH (a)-[r]->(b) WHERE type(r) IN $types
              AND any(label IN labels(a) WHERE label IN $labels)
              AND any(label IN labels(b) WHERE label IN $labels)
            """;

    public static void main(String[] args) {
        boolean confirmed = Arrays.asList(args).equals(java.util.List.of("--confirm-clear"));
        try (var context = new SpringApplicationBuilder(ClearGameHubGraph.class)
                .web(WebApplicationType.NONE).run(args)) {
            var config = context.getEnvironment();
            String uri = config.getRequiredProperty("spring.neo4j.uri");
            var auth = AuthTokens.basic(config.getRequiredProperty("spring.neo4j.authentication.username"),
                    config.getRequiredProperty("spring.neo4j.authentication.password"));
            var labels = GraphNodeMapping.ALL.stream().map(GraphNodeMapping::label).toList();
            var types = GraphRelationshipMapping.ALL.stream().map(GraphRelationshipMapping::type).distinct().toList();
            var params = Map.<String, Object>of("labels", labels, "types", types);
            try (var driver = GraphDatabase.driver(uri, auth);
                 var session = driver.session();
                 var tx = session.beginTransaction()) {
                if (!confirmed) {
                    long nodes = tx.run(NODES + "RETURN count(n)", params).single().get(0).asLong();
                    long relationships = tx.run(RELATIONSHIPS + "RETURN count(r)", params).single().get(0).asLong();
                    System.out.printf("%s: %d nodes and %d relationships. Labels: %s%n", uri, nodes, relationships, labels);
                    return;
                }
                int relationships = tx.run(RELATIONSHIPS + "DELETE r", params).consume().counters().relationshipsDeleted();
                // Plain DELETE rolls back the cleanup if a node still has an unrelated relationship.
                int nodes = tx.run(NODES + "DELETE n", params).consume().counters().nodesDeleted();
                tx.commit();
                System.out.printf("Deleted %d GameHub nodes and %d relationships.%n", nodes, relationships);
            }
        }
    }
}
