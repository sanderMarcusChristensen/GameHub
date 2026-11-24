package com.gamehub.mapper;

import com.gamehub.dto.PlayerRequest;
import com.gamehub.dto.PlayerResponse;
import com.gamehub.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerRequest request) {
        return Player.builder()
                .nickname(request.nickname())
                .build();
    }

    public PlayerResponse toResponse(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getNickname()
        );
    }
}
