package com.gamehub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gamehub.service.CitoAPIService;

@RestController
public class CitoAPIController {

    private final CitoAPIService citoAPIClient;

    public CitoAPIController(CitoAPIService citoAPIClient) {
        this.citoAPIClient = citoAPIClient;
    }

    @GetMapping("/api/cito/lol/players/{playerIdOrSlug}/stats")
    public String getPlayerStats(@PathVariable String playerIdOrSlug) {
        return citoAPIClient.getPlayerStats(playerIdOrSlug);
    }
}
