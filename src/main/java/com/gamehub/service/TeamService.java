package com.gamehub.service;

import com.gamehub.dto.TeamDto;
import com.gamehub.dto.TeamRequestDto;
import com.gamehub.entity.jpa.Team;
import com.gamehub.exception.ResourceConflictException;
import com.gamehub.exception.ResourceNotFoundException;
import com.gamehub.mapper.TeamMapper;
import com.gamehub.repository.jpa.TeamRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teams;
    private final TeamMapper mapper;

    public TeamService(TeamRepository teams, TeamMapper mapper) {
        this.teams = teams;
        this.mapper = mapper;
    }

    // Save a new team; the database also enforces unique names.
    public TeamDto create(TeamRequestDto dto) {
        String name = dto.name().trim();
        if (teams.existsByName(name)) {
            throw new ResourceConflictException("A team with this name already exists.");
        }
        try {
            return mapper.toDto(teams.saveAndFlush(mapper.toEntity(dto)));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("The team could not be saved; the name may already exist.");
        }
    }

    // Fetch one team or report that it is missing.
    public TeamDto getById(Long id) {
        return mapper.toDto(findById(id));
    }

    // Convert all stored teams to API responses.
    public List<TeamDto> getAll() {
        return teams.findAll().stream().map(mapper::toDto).toList();
    }

    // Update the editable fields without changing the ID or creation date.
    public TeamDto update(Long id, TeamRequestDto dto) {
        Team team = findById(id);
        String name = dto.name().trim();
        if (teams.existsByNameAndIdNot(name, id)) {
            throw new ResourceConflictException("A team with this name already exists.");
        }
        team.setName(name);
        team.setShortName(dto.shortName());
        try {
            return mapper.toDto(teams.saveAndFlush(team));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("The team could not be saved; the name may already exist.");
        }
    }

    // A team referenced by other records cannot be deleted.
    public void deleteById(Long id) {
        Team team = findById(id);
        try {
            teams.delete(team);
            teams.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("The team cannot be deleted because it is in use.");
        }
    }

    private Team findById(Long id) {
        return teams.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team with id " + id + " not found."));
    }
}
