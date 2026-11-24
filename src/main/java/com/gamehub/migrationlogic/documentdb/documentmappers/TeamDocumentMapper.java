package com.gamehub.migrationlogic.documentdb.documentmappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gamehub.entity.document.TeamDocument;
import com.gamehub.entity.jpa.Team;

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