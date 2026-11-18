package com.gamehub.service;

import com.gamehub.dto.PlayerDto;
import com.gamehub.entity.Player;
import com.gamehub.exception.ResourceConflictException;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.mapper.PlayerMapper;
import com.gamehub.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerDto create(PlayerDto playerDto) {
        if (playerRepository.existsByNickname(playerDto.nickname())) {
            throw new ResourceConflictException("Player with nickname '" + playerDto.nickname() + "' already exists.");
        }

        Player player = playerMapper.toEntity(playerDto);
        Player savedPlayer = playerRepository.save(player);

        return playerMapper.toDto(savedPlayer);
    }

    public PlayerDto getByNickname(String nickname) {
        return playerRepository.findByNickname(nickname)
                .map(playerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Player with nickname '" + nickname + "' not found."));
    }

    public PlayerDto getById(Long id) {
        return playerMapper.toDto(findById(id));
    }

    public List<PlayerDto> getAll() {
        return playerRepository.findAll()
                .stream().map(playerMapper::toDto)
                .toList();
    }

    public PlayerDto update(Long id, PlayerDto dto) {
        Player player = findById(id);

        if (!player.getNickname().equals(dto.nickname()) && playerRepository.existsByNickname(dto.nickname())) {
            throw new ResourceConflictException("Player with nickname '" + dto.nickname() + "' already exists.");
        }

        player.setNickname(dto.nickname());

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toDto(savedPlayer);
    }

    public void deleteById(Long id) {
        playerRepository.delete(findById(id));
    }

    private Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player with id " + id + " not found."));
    }
}