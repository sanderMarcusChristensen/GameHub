package com.gamehub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamehub.dto.GameResultRequest;
import com.gamehub.service.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/{id}/games/results")
    public ResponseEntity<Void> recordGameResult(
            @PathVariable Long id,
            @RequestBody GameResultRequest request) {

        matchService.recordGameResult(id, request);

        return ResponseEntity.noContent().build();
    }
}
