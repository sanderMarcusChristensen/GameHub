package com.gamehub.mapper;

import com.gamehub.dto.PlayerDto;
import com.gamehub.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerDto playerDto) {
        return Player.builder()
                .nickname(playerDto.nickname())
                .build();
    }

    public PlayerDto toDto(Player player) {
        return new PlayerDto(
                player.getId(),
                player.getNickname()
        );
    }
}
