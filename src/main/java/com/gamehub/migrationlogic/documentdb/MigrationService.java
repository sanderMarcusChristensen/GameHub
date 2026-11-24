
package com.gamehub.migrationlogic.documentdb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.gamehub.entity.document.ChampionDocument;
import com.gamehub.entity.document.MatchDocument;
import com.gamehub.entity.document.PlayerDocument;
import com.gamehub.entity.document.TeamDocument;
import com.gamehub.entity.document.TournamentDocument;
import com.gamehub.entity.document.UserDocument;
import com.gamehub.entity.jpa.AppUser;
import com.gamehub.entity.jpa.Champion;
import com.gamehub.entity.jpa.Game;
import com.gamehub.entity.jpa.Match;
import com.gamehub.entity.jpa.MatchStatusHistory;
import com.gamehub.entity.jpa.Player;
import com.gamehub.entity.jpa.PlayerGameStats;
import com.gamehub.entity.jpa.PlayerStatistics;
import com.gamehub.entity.jpa.Team;
import com.gamehub.entity.jpa.TeamPlayer;
import com.gamehub.entity.jpa.Tournament;
import com.gamehub.entity.jpa.TournamentTeam;
import com.gamehub.migrationlogic.documentdb.documentmappers.ChampionDocumentMapper;
import com.gamehub.migrationlogic.documentdb.documentmappers.MatchDocumentMapper;
import com.gamehub.migrationlogic.documentdb.documentmappers.PlayerDocumentMapper;
import com.gamehub.migrationlogic.documentdb.documentmappers.TeamDocumentMapper;
import com.gamehub.migrationlogic.documentdb.documentmappers.TournamentDocumentMapper;
import com.gamehub.migrationlogic.documentdb.documentmappers.UserDocumentMapper;
import com.gamehub.repository.document.ChampionDocumentRepository;
import com.gamehub.repository.document.MatchDocumentRepository;
import com.gamehub.repository.document.PlayerDocumentRepository;
import com.gamehub.repository.document.TeamDocumentRepository;
import com.gamehub.repository.document.TournamentDocumentRepository;
import com.gamehub.repository.document.UserDocumentRepository;
import com.gamehub.repository.jpa.AppUserRepository;
import com.gamehub.repository.jpa.ChampionRepository;
import com.gamehub.repository.jpa.GameRepository;
import com.gamehub.repository.jpa.MatchRepository;
import com.gamehub.repository.jpa.MatchStatusHistoryRepository;
import com.gamehub.repository.jpa.PlayerGameStatsRepository;
import com.gamehub.repository.jpa.PlayerRepository;
import com.gamehub.repository.jpa.PlayerStatisticsRepository;
import com.gamehub.repository.jpa.TeamPlayerRepository;
import com.gamehub.repository.jpa.TeamRepository;
import com.gamehub.repository.jpa.TournamentRepository;
import com.gamehub.repository.jpa.TournamentTeamRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
@Profile("migrate-mongo")
public class MigrationService implements CommandLineRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(MigrationService.class);

    private final PlayerRepository playerRepository;
    private final TeamPlayerRepository teamPlayerRepository;
    private final PlayerStatisticsRepository playerStatisticsRepository;

    private final PlayerDocumentRepository playerDocumentRepository;
    private final PlayerDocumentMapper playerDocumentMapper;

    private final TeamRepository teamRepository;
    private final TeamDocumentRepository teamDocumentRepository;
    private final TeamDocumentMapper teamDocumentMapper;

    private final ChampionRepository championRepository;
    private final ChampionDocumentRepository championDocumentRepository;
    private final ChampionDocumentMapper championDocumentMapper;

    private final TournamentRepository tournamentRepository;
    private final TournamentTeamRepository tournamentTeamRepository;
    private final TournamentDocumentRepository tournamentDocumentRepository;
    private final TournamentDocumentMapper tournamentDocumentMapper;

    private final MatchRepository matchRepository;
    private final GameRepository gameRepository;
    private final PlayerGameStatsRepository playerGameStatsRepository;
    private final MatchStatusHistoryRepository matchStatusHistoryRepository;
    private final MatchDocumentRepository matchDocumentRepository;
    private final MatchDocumentMapper matchDocumentMapper;

    private final AppUserRepository appUserRepository;
    private final UserDocumentRepository userDocumentRepository;
    private final UserDocumentMapper userDocumentMapper;

    @Override
    public void run(String... args) {

        logger.info("Starting MySQL to MongoDB migration...");

        migratePlayers();
        migrateTeams();
        migrateChampions();
        migrateTournaments();
        migrateMatches();
        migrateUsers();

        logger.info("Migration finished!");
    }

    

    private void migratePlayers() {

        logger.info("Starting player migration...");

        // 1. Extract data from MySQL.
        List<Player> players = playerRepository.findAll();

        List<TeamPlayer> teamPlayers = teamPlayerRepository.findAll();

        List<PlayerStatistics> playerStatistics =
                playerStatisticsRepository.findAll();

        logger.info("Found {} players", players.size());
        logger.info("Found {} team memberships", teamPlayers.size());
        logger.info("Found {} player statistics records",
                playerStatistics.size());

        // 2. Transform relational data into MongoDB documents.
        List<PlayerDocument> playerDocuments =
                playerDocumentMapper.mapAll(
                        players,
                        teamPlayers,
                        playerStatistics
                );

        // 3. Load documents into MongoDB.
        playerDocumentRepository.saveAll(playerDocuments);

        logger.info("Successfully migrated {} players",
                playerDocuments.size());
    }

    private void migrateTeams() {

    logger.info("Starting team migration...");

    // Extract
    List<Team> teams = teamRepository.findAll();

    logger.info("Found {} teams", teams.size());

    // Transform
    List<TeamDocument> teamDocuments =
            teamDocumentMapper.mapAll(teams);

    // Load
    teamDocumentRepository.saveAll(teamDocuments);

    logger.info("Successfully migrated {} teams",
            teamDocuments.size());
}

private void migrateChampions() {
    logger.info("Starting champion migration...");

    List<Champion> champions = championRepository.findAllWithRoles();

    logger.info("Found {} champions", champions.size());

    List<ChampionDocument> championDocuments =
            championDocumentMapper.mapAll(champions);

    championDocumentRepository.saveAll(championDocuments);

    logger.info("Successfully migrated {} champions",
            championDocuments.size());
}

private void migrateTournaments() {
    logger.info("Starting tournament migration...");

    List<Tournament> tournaments = tournamentRepository.findAll();
    List<TournamentTeam> tournamentTeams = tournamentTeamRepository.findAll();

    logger.info("Found {} tournaments", tournaments.size());
    logger.info("Found {} tournament registrations", tournamentTeams.size());

    List<TournamentDocument> tournamentDocuments =
            tournamentDocumentMapper.mapAll(tournaments, tournamentTeams);

    tournamentDocumentRepository.saveAll(tournamentDocuments);

    logger.info("Successfully migrated {} tournaments",
            tournamentDocuments.size());
}


private void migrateMatches() {
    logger.info("Starting match migration...");

    List<Match> matches = matchRepository.findAll();
    List<Game> games = gameRepository.findAll();
    List<PlayerGameStats> playerGameStats =
            playerGameStatsRepository.findAll();
    List<MatchStatusHistory> statusHistory =
            matchStatusHistoryRepository.findAll();

    logger.info("Found {} matches", matches.size());
    logger.info("Found {} games", games.size());
    logger.info("Found {} player game statistics", playerGameStats.size());
    logger.info("Found {} match status history records", statusHistory.size());

    List<MatchDocument> matchDocuments =
            matchDocumentMapper.mapAll(
                    matches,
                    games,
                    playerGameStats,
                    statusHistory
            );

    matchDocumentRepository.saveAll(matchDocuments);

    logger.info("Successfully migrated {} matches",
            matchDocuments.size());
}


private void migrateUsers() {
    logger.info("Starting user migration...");

    List<AppUser> users = appUserRepository.findAll();

    logger.info("Found {} users", users.size());

    List<UserDocument> userDocuments =
            userDocumentMapper.mapAll(users);

    userDocumentRepository.saveAll(userDocuments);

    logger.info("Successfully migrated {} users",
            userDocuments.size());
}


}
