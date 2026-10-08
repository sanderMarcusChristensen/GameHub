-- GameHub v1 updated to match ERD_v6
-- MySQL 8.0+
-- Schema based on ERD_v6.sql
-- Run in an empty database.

CREATE SCHEMA IF NOT EXISTS lol_esports CHARACTER SET utf8mb4;
USE lol_esports;

CREATE TABLE players (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    ign VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE teams (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(20) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE team_players (
    team_id BIGINT UNSIGNED NOT NULL,
    player_id BIGINT UNSIGNED NOT NULL,
    joined_at DATETIME NOT NULL,
    left_at DATETIME NULL,
    PRIMARY KEY (team_id, player_id),
    KEY fk_team_players_player (player_id),
    CONSTRAINT fk_team_players_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_team_players_player FOREIGN KEY (player_id) REFERENCES players(id)
) ENGINE=InnoDB;

CREATE TABLE champions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    released_at DATE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE tournaments (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    start_date DATETIME NULL,
    end_date DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE tournament_teams (
    tournament_id BIGINT UNSIGNED NOT NULL,
    team_id BIGINT UNSIGNED NOT NULL,
    registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tournament_id, team_id),
    KEY fk_tournament_teams_team (team_id),
    CONSTRAINT fk_tournament_teams_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    CONSTRAINT fk_tournament_teams_team FOREIGN KEY (team_id) REFERENCES teams(id)
) ENGINE=InnoDB;

CREATE TABLE matches (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    tournament_id BIGINT UNSIGNED NOT NULL,
    team_a_id BIGINT UNSIGNED NOT NULL,
    team_b_id BIGINT UNSIGNED NOT NULL,
    format ENUM('BO1', 'BO3', 'BO5') NOT NULL,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    winner_team_id BIGINT UNSIGNED NULL,
    scheduled_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY fk_matches_tournament (tournament_id),
    KEY fk_matches_team_a (team_a_id),
    KEY fk_matches_team_b (team_b_id),
    KEY fk_matches_winner (winner_team_id),
    CONSTRAINT fk_matches_tournament FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    CONSTRAINT fk_matches_team_a FOREIGN KEY (team_a_id) REFERENCES teams(id),
    CONSTRAINT fk_matches_team_b FOREIGN KEY (team_b_id) REFERENCES teams(id),
    CONSTRAINT fk_matches_winner FOREIGN KEY (winner_team_id) REFERENCES teams(id)
) ENGINE=InnoDB;

CREATE TABLE games (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    match_id BIGINT UNSIGNED NOT NULL,
    game_number INT UNSIGNED NOT NULL,
    winner_team_id BIGINT UNSIGNED NULL,
    started_at DATETIME NULL,
    ended_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_game_number_per_match (match_id, game_number),
    KEY fk_games_match (match_id),
    KEY fk_games_winner (winner_team_id),
    CONSTRAINT fk_games_match FOREIGN KEY (match_id) REFERENCES matches(id),
    CONSTRAINT fk_games_winner FOREIGN KEY (winner_team_id) REFERENCES teams(id)
) ENGINE=InnoDB;

CREATE TABLE player_game_stats (
    game_id BIGINT UNSIGNED NOT NULL,
    player_id BIGINT UNSIGNED NOT NULL,
    team_id BIGINT UNSIGNED NOT NULL,
    champion_id BIGINT UNSIGNED NOT NULL,
    kills INT UNSIGNED NOT NULL DEFAULT 0,
    deaths INT UNSIGNED NOT NULL DEFAULT 0,
    assists INT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (game_id, player_id),
    KEY fk_player_game_stats_player (player_id),
    KEY fk_player_game_stats_team (team_id),
    KEY fk_player_game_stats_champion (champion_id),
    CONSTRAINT fk_player_game_stats_game FOREIGN KEY (game_id) REFERENCES games(id),
    CONSTRAINT fk_player_game_stats_player FOREIGN KEY (player_id) REFERENCES players(id),
    CONSTRAINT fk_player_game_stats_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_player_game_stats_champion FOREIGN KEY (champion_id) REFERENCES champions(id)
) ENGINE=InnoDB;

CREATE TABLE champion_roles (
    id INT NOT NULL,
    name VARCHAR(45) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE champions_has_champion_roles (
    champions_id BIGINT UNSIGNED NOT NULL,
    champion_roles_id INT NOT NULL,
    PRIMARY KEY (champions_id, champion_roles_id),
    KEY fk_champions_has_champion_roles_champion_roles1_idx (champion_roles_id),
    KEY fk_champions_has_champion_roles_champions1_idx (champions_id),
    CONSTRAINT fk_champions_has_champion_roles_champions1
        FOREIGN KEY (champions_id) REFERENCES champions(id),
    CONSTRAINT fk_champions_has_champion_roles_champion_roles1
        FOREIGN KEY (champion_roles_id) REFERENCES champion_roles(id)
) ENGINE=InnoDB;
