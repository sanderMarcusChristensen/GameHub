package com.gamehub.migrationlogic.graphdb;

import com.gamehub.migrationlogic.graphdb.graphmappers.GraphPropertyMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("migrate-neo4j")
@RequiredArgsConstructor
public class GraphMigration implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(GraphMigration.class);

    private final DataSource dataSource;
    private final GraphPropertyMapper graphPropertyMapper;
    private final GraphMigrationRepository graphRepository;

    @Override
    public void run(String... args) throws SQLException {
        logger.info("Starting MySQL to Neo4j migration...");

        MySqlSnapshot snapshot = readSnapshot();
        var graphData = graphPropertyMapper.mapAll(snapshot);

        graphRepository.createConstraints();
        graphRepository.saveAndVerify(graphData);

        graphData.forEach((table, rows) -> logger.info("{}: {} verified rows", table, rows.size()));
        logger.info("Neo4j migration completed and verified.");
    }

    private MySqlSnapshot readSnapshot() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            return MySqlSnapshot.read(connection);
        }
    }
}
