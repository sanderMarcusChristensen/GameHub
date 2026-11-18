package com.gamehub.graphrepository;

import java.util.Optional;
import com.gamehub.entity.graph.TeamNode;
import org.springframework.data.repository.Repository;

// Read-only until the graph relationships and migration are implemented.
public interface TeamGraphRepository extends Repository<TeamNode, Long> {
    Optional<TeamNode> findById(Long id);
}
