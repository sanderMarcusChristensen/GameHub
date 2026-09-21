package com.gamehub.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CitoAPIService {
    @Value("${cito.api-key}")
private String apiKey;
    private final RestClient restClient;

    public CitoAPIService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.citoapi.com")
                .build();
    }


public String getPlayerStats(String playerIdOrSlug) {

    return restClient.get()
            .uri("/api/v1/lol/players/" + playerIdOrSlug + "/stats")
            .header("x-api-key", apiKey)
            .retrieve()
            .body(String.class);
}

}