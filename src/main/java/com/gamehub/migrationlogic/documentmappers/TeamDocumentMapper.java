package com.gamehub.migrationlogic.documentmappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gamehub.entity.Team;
import com.gamehub.entity.document.TeamDocument;

@Component
public class TeamDocumentMapper {

    public List<TeamDocument> mapAll(List<Team> teams) {

        return teams.stream()
                .map(team -> new TeamDocument(
                        team.getId(),
                        team.getName(),
                        team.getShortName(),
                        team.getCreatedAt()
                ))
                .toList();
    }
}