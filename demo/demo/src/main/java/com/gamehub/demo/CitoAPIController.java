package com.gamehub.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class CitoAPIController {

    private final CitoAPIService citoAPIService;

    public CitoAPIController(CitoAPIService citoAPIService) {
        this.citoAPIService = citoAPIService;
    }
    
//http://localhost:8080/api/cito/lol/players/faker/stats as an example of how to use the endpoint
@GetMapping("/api/cito/lol/players/{playerIdOrSlug}/stats")
public String getPlayerStats(@PathVariable String playerIdOrSlug) {

    return citoAPIService.getPlayerStats(playerIdOrSlug);
}

}