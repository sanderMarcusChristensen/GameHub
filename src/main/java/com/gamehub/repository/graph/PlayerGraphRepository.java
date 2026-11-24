package com.gamehub.repository.graph;

import java.util.Optional;
import com.gamehub.entity.graph.PlayerNode;
import org.springframework.data.repository.Repository;

public interface PlayerGraphRepository extends Repository<PlayerNode, Long>, PlayerTeamHistoryReader {
    Optional<PlayerNode> findById(Long id);
}
