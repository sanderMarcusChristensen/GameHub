package com.gamehub.service;

import com.gamehub.dto.PlayerTeamHistory;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.graphrepository.PlayerGraphRepository;
import org.springframework.stereotype.Service;

@Service
public class GraphPlayerService {
    private final PlayerGraphRepository players;

    public GraphPlayerService(PlayerGraphRepository players) {
        this.players = players;
    }

    public PlayerTeamHistory getTeamHistory(long id) {
        return players.findTeamHistory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player " + id + " was not found in Neo4j."));
    }
}
