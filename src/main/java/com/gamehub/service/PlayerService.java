package com.gamehub.service;

import com.gamehub.dto.PlayerFilterDto;
import com.gamehub.dto.PlayerRequest;
import com.gamehub.dto.PlayerResponse;
import com.gamehub.entity.Player;
import com.gamehub.exception.ResourceConflictException;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.mapper.PlayerMapper;
import com.gamehub.repository.PlayerRepository;
import com.gamehub.specification.PlayerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerResponse create(PlayerRequest request) {
        if (playerRepository.existsByNickname(request.nickname())) {
            throw new ResourceConflictException("Player with nickname '" + request.nickname() + "' already exists.");
        }

        Player player = playerMapper.toEntity(request);
        Player savedPlayer = playerRepository.save(player);

        return playerMapper.toResponse(savedPlayer);
    }

    public PlayerResponse getById(Long id) {
        return playerMapper.toResponse(findById(id));
    }

    public Page<PlayerResponse> getAll(PlayerFilterDto filter, Pageable pageable
    ) {
        Specification<Player> spec = Specification.unrestricted();

        if (filter.nickname() != null && !filter.nickname().isBlank()) {
            spec = spec.and(PlayerSpecification.nicknameContains(filter.nickname()));
        }

        return playerRepository.findAll(spec, pageable)
                .map(playerMapper::toResponse);
    }

    public PlayerResponse update(Long id, PlayerRequest request) {
        Player player = findById(id);

        if (!player.getNickname().equals(request.nickname()) && playerRepository.existsByNickname(request.nickname())) {
            throw new ResourceConflictException("Player with nickname '" + request.nickname() + "' already exists.");
        }

        player.setNickname(request.nickname());

        Player savedPlayer = playerRepository.save(player);
        return playerMapper.toResponse(savedPlayer);
    }

    public void deleteById(Long id) {
        playerRepository.delete(findById(id));
    }

    private Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player with id " + id + " not found."));
    }
}
