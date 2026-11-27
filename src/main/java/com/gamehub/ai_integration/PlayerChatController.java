package com.gamehub.ai_integration;

import java.io.IOException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/chat")
@ConditionalOnProperty(name = "gamehub.ai.enabled", havingValue = "true")
public class PlayerChatController {
    private final PlayerChatService chatService;

    public PlayerChatController(PlayerChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public PlayerAnalysisController.AnalysisResponse chat(@Valid @RequestBody ChatRequest request) throws IOException {
        return new PlayerAnalysisController.AnalysisResponse(chatService.answer(request.question()));
    }

    public record ChatRequest(@NotBlank @Size(max = 1000) String question) {
    }
}
