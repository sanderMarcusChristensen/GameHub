package com.gamehub.graphrepository;

import java.util.Optional;
import com.gamehub.entity.graph.PlayerNode;
import org.springframework.data.repository.Repository;

// Read-only until the graph relationships and migration are implemented.
public interface PlayerGraphRepository extends Repository<PlayerNode, Long> {
    Optional<PlayerNode> findById(Long id);
}
