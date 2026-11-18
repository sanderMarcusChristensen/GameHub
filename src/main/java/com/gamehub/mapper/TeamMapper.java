package com.gamehub.mapper;

import com.gamehub.dto.TeamDto;
import com.gamehub.dto.TeamRequestDto;
import com.gamehub.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
    // Turn the incoming data into a new entity.
    public Team toEntity(TeamRequestDto dto) {
        return new Team(dto.name().trim(), dto.shortName());
    }

    // Return only the fields included in the API response.
    public TeamDto toDto(Team team) {
        return new TeamDto(team.getId(), team.getName(), team.getShortName(), team.getCreatedAt());
    }
}
