package com.gamehub.graph;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Neo4jConnectionTest {

    @Test
    void connectsToNeo4j() {
        String uri = System.getenv().getOrDefault("NEO4J_URI", "bolt://localhost:7687");
        String username = System.getenv().getOrDefault("NEO4J_USERNAME", "neo4j");
        String password = System.getenv().getOrDefault("NEO4J_PASSWORD", "gamehub-local-demo");

        try (var driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
             var session = driver.session()) {
            long result = session.run("RETURN 1 AS value").single().get("value").asLong();
            assertEquals(1L, result);
        }
    }
}
