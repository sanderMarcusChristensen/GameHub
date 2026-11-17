package com.gamehub.graph;

import com.gamehub.entity.graph.PlayerNode;
import com.gamehub.graphrepository.PlayerGraphRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "cito.api-key=test-key",
        "spring.mongodb.uri=mongodb://localhost:27017/test"
})
class PlayerGraphRepositoryTest {
    @Autowired
    private PlayerGraphRepository players;

    @Test
    void findsTheManuallyCreatedTestPlayer() {
        var result = players.findById(-1L);

        assertTrue(result.isPresent(), "Create the RepositoryTest player in Neo4j first");
        PlayerNode player = result.orElseThrow();
        assertEquals(-1L, player.id());
        assertEquals("Repository test player", player.nickname());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), player.createdAt());
    }
}
