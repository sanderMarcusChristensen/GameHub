package com.gamehub.controller;

import com.gamehub.dto.PlayerTeamHistory;
import com.gamehub.service.GraphPlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph/players")
public class GraphPlayerController {
    private final GraphPlayerService players;

    public GraphPlayerController(GraphPlayerService players) {
        this.players = players;
    }

    @GetMapping("/{id}/teams")
    public PlayerTeamHistory getTeams(@PathVariable long id) {
        return players.getTeamHistory(id);
    }
}
