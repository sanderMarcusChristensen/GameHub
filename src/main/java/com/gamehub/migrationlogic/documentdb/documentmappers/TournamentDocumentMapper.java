package com.gamehub.migrationlogic.documentdb.documentmappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gamehub.entity.Tournament;
import com.gamehub.entity.TournamentTeam;
import com.gamehub.entity.document.TournamentDocument;

@Component
public class TournamentDocumentMapper {

    public List<TournamentDocument> mapAll(
            List<Tournament> tournaments,
            List<TournamentTeam> tournamentTeams) {

        Map<Long, List<TournamentTeam>> teamsByTournament =
                tournamentTeams.stream()
                        .collect(Collectors.groupingBy(
                                tt -> tt.getId().getTournamentId()
                        ));

        return tournaments.stream()
                .map(tournament -> {
                    List<TournamentDocument.RegisteredTeam> registeredTeams =
                            teamsByTournament
                                    .getOrDefault(tournament.getId(), List.of())
                                    .stream()
                                    .map(tt -> new TournamentDocument.RegisteredTeam(
                                            tt.getId().getTeamId(),
                                            tt.getRegisteredAt()
                                    ))
                                    .toList();

                    return new TournamentDocument(
                            tournament.getId(),
                            tournament.getName(),
                            tournament.getStartDate() == null
                                    ? null : tournament.getStartDate().toLocalDate(),
                            tournament.getEndDate() == null
                                    ? null : tournament.getEndDate().toLocalDate(),
                            tournament.getCreatedAt(),
                            registeredTeams
                    );
                })
                .toList();
    }
}
