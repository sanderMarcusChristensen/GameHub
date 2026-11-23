package com.gamehub.migrationlogic.graphdb;

import com.gamehub.migrationlogic.graphdb.graphmappers.GraphNodeMapping;
import com.gamehub.migrationlogic.graphdb.graphmappers.GraphRelationshipMapping;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.neo4j.autoconfigure.Neo4jProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("clear-neo4j & !migrate-neo4j")
@RequiredArgsConstructor
public class ClearGameHubGraph implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClearGameHubGraph.class);
    private static final String NODES =
            "MATCH (n) WHERE any(label IN labels(n) WHERE label IN $labels) ";
    private static final String RELATIONSHIPS = """
            MATCH (a)-[r]->(b) WHERE type(r) IN $types
              AND any(label IN labels(a) WHERE label IN $labels)
              AND any(label IN labels(b) WHERE label IN $labels)
            """;

    private final Neo4jClient neo4j;
    private final Neo4jProperties config;

    @Override
    @Transactional
    public void run(String... args) {
        List<String> labels = GraphNodeMapping.ALL.stream()
                .map(GraphNodeMapping::label)
                .toList();
        List<String> types = GraphRelationshipMapping.ALL.stream()
                .map(GraphRelationshipMapping::type)
                .distinct()
                .toList();
        Map<String, Object> parameters = Map.of("labels", labels, "types", types);

        boolean confirmed = args.length == 1 && "--confirm-clear".equals(args[0]);
        if (confirmed) {
            clearGraph(parameters);
        } else {
            long nodes = count(NODES + "RETURN count(n)", parameters);
            long relationships = count(RELATIONSHIPS + "RETURN count(r)", parameters);
            logger.info("{}: {} nodes and {} relationships. Labels: {}", config.getUri(), nodes, relationships, labels);
        }
    }

    private void clearGraph(Map<String, Object> parameters) {
        int relationships = neo4j.query(RELATIONSHIPS + "DELETE r").bindAll(parameters)
                .run().counters().relationshipsDeleted();
        // DELETE fails and rolls back both queries if a node still has an unrelated relationship.
        int nodes = neo4j.query(NODES + "DELETE n").bindAll(parameters).run().counters().nodesDeleted();
        logger.info("Deleted {} GameHub nodes and {} relationships.", nodes, relationships);
    }

    private long count(String query, Map<String, Object> parameters) {
        return neo4j.query(query).bindAll(parameters).fetchAs(Long.class).one().orElseThrow();
    }
}
