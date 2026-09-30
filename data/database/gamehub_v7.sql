-- ============================================================
-- GameHub: League of Legends Esports
-- Requires MySQL 8.0.16 or newer.
--
-- Run once to create a NEW database.
-- This script does not migrate an existing database.
--
-- Use the stored procedures for:
--   - recording or correcting game results;
--   - saving player statistics;
--   - saving team membership periods.
--
-- These procedures own their transactions.
-- Call them outside an existing transaction.
--
-- Assumptions:
--   - Matches use BO1, BO3 or BO5.
--   - Games have no draws.
--   - Match winners are determined by game wins.
--   - Walkovers and forfeits are not included.
--   - Game rows are created when their results are recorded.
-- ============================================================

CREATE DATABASE lol_esports_v2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE lol_esports_v2;


-- ============================================================
-- 1. TABLES
-- ============================================================

CREATE TABLE players (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nickname VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = InnoDB;


CREATE TABLE teams (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(20) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = InnoDB;


CREATE TABLE team_players (
    team_id BIGINT UNSIGNED NOT NULL,
    player_id BIGINT UNSIGNED NOT NULL,
    joined_at DATETIME NOT NULL,
    left_at DATETIME NULL,

    -- A player can return to the same team at a later date.
    PRIMARY KEY (team_id, player_id, joined_at),

    CONSTRAINT fk_team_players_team
        FOREIGN KEY (team_id) REFERENCES teams (id),

    CONSTRAINT fk_team_players_player
        FOREIGN KEY (player_id) REFERENCES players (id),

    CONSTRAINT chk_membership_dates
        CHECK (left_at IS NULL OR left_at > joined_at)
) ENGINE = InnoDB;


CREATE TABLE champions (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,

    -- Enter the actual release date.
    released_at DATE NOT NULL,

    PRIMARY KEY (id)
) ENGINE = InnoDB;


CREATE TABLE champion_roles (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_champion_role_name (name)
) ENGINE = InnoDB;


CREATE TABLE champions_has_champion_roles (
    champions_id BIGINT UNSIGNED NOT NULL,
    champion_roles_id INT NOT NULL,

    PRIMARY KEY (champions_id, champion_roles_id),

    CONSTRAINT fk_champion_role_champion
        FOREIGN KEY (champions_id) REFERENCES champions (id),

    CONSTRAINT fk_champion_role_role
        FOREIGN KEY (champion_roles_id) REFERENCES champion_roles (id)
) ENGINE = InnoDB;


CREATE TABLE tournaments (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    start_date DATETIME NULL,
    end_date DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT chk_tournament_dates
        CHECK (
            end_date IS NULL
            OR (
                start_date IS NOT NULL
                AND end_date >= start_date
            )
        )
) ENGINE = InnoDB;


CREATE TABLE tournament_teams (
    tournament_id BIGINT UNSIGNED NOT NULL,
    team_id BIGINT UNSIGNED NOT NULL,
    registered_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (tournament_id, team_id),

    CONSTRAINT fk_tournament_teams_tournament
        FOREIGN KEY (tournament_id) REFERENCES tournaments (id),

    CONSTRAINT fk_tournament_teams_team
        FOREIGN KEY (team_id) REFERENCES teams (id)
) ENGINE = InnoDB;


CREATE TABLE matches (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    tournament_id BIGINT UNSIGNED NOT NULL,
    team_a_id BIGINT UNSIGNED NOT NULL,
    team_b_id BIGINT UNSIGNED NOT NULL,

    format ENUM('BO1', 'BO3', 'BO5') NOT NULL,

    status ENUM(
        'SCHEDULED',
        'IN_PROGRESS',
        'COMPLETED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'SCHEDULED',

    winner_team_id BIGINT UNSIGNED NULL,
    scheduled_at DATETIME NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_matches_tournament
        FOREIGN KEY (tournament_id) REFERENCES tournaments (id),

    -- Both participants must be registered for this tournament.
    CONSTRAINT fk_matches_registered_team_a
        FOREIGN KEY (tournament_id, team_a_id)
        REFERENCES tournament_teams (tournament_id, team_id),

    CONSTRAINT fk_matches_registered_team_b
        FOREIGN KEY (tournament_id, team_b_id)
        REFERENCES tournament_teams (tournament_id, team_id),

    CONSTRAINT fk_matches_winner
        FOREIGN KEY (winner_team_id) REFERENCES teams (id),

    CONSTRAINT chk_different_match_teams
        CHECK (team_a_id <> team_b_id),

    CONSTRAINT chk_match_winner_participates
        CHECK (
            winner_team_id IS NULL
            OR winner_team_id IN (team_a_id, team_b_id)
        ),

    CONSTRAINT chk_match_completion
        CHECK (
            (
                status = 'COMPLETED'
                AND winner_team_id IS NOT NULL
            )
            OR (
                status <> 'COMPLETED'
                AND winner_team_id IS NULL
            )
        )
) ENGINE = InnoDB;


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

    CONSTRAINT fk_games_match
        FOREIGN KEY (match_id) REFERENCES matches (id),

    CONSTRAINT fk_games_winner
        FOREIGN KEY (winner_team_id) REFERENCES teams (id),

    CONSTRAINT chk_positive_game_number
        CHECK (game_number > 0),

    CONSTRAINT chk_game_dates
        CHECK (
            ended_at IS NULL
            OR (
                started_at IS NOT NULL
                AND ended_at >= started_at
            )
        ),

    CONSTRAINT chk_game_completion
        CHECK (
            (
                winner_team_id IS NULL
                AND ended_at IS NULL
            )
            OR (
                winner_team_id IS NOT NULL
                AND ended_at IS NOT NULL
            )
        )
) ENGINE = InnoDB;


CREATE TABLE player_game_stats (
    game_id BIGINT UNSIGNED NOT NULL,
    player_id BIGINT UNSIGNED NOT NULL,
    team_id BIGINT UNSIGNED NOT NULL,
    champion_id BIGINT UNSIGNED NOT NULL,

    kills INT UNSIGNED NOT NULL DEFAULT 0,
    deaths INT UNSIGNED NOT NULL DEFAULT 0,
    assists INT UNSIGNED NOT NULL DEFAULT 0,

    PRIMARY KEY (game_id, player_id),

    CONSTRAINT fk_stats_game
        FOREIGN KEY (game_id) REFERENCES games (id),

    CONSTRAINT fk_stats_player
        FOREIGN KEY (player_id) REFERENCES players (id),

    CONSTRAINT fk_stats_team
        FOREIGN KEY (team_id) REFERENCES teams (id),

    CONSTRAINT fk_stats_champion
        FOREIGN KEY (champion_id) REFERENCES champions (id)
) ENGINE = InnoDB;


-- ============================================================
-- 2. STORED FUNCTIONS
-- ============================================================

DELIMITER $$


-- Returns the team's match win rate as a percentage.
-- Only completed matches are included.
-- Returns NULL when the team has no completed matches.
CREATE FUNCTION fn_team_match_win_rate (
    p_team_id BIGINT UNSIGNED
)
RETURNS DECIMAL(5,2)
NOT DETERMINISTIC
READS SQL DATA
SQL SECURITY INVOKER
BEGIN
    DECLARE v_win_rate DECIMAL(5,2);

    IF NOT EXISTS (
        SELECT 1 FROM teams WHERE id = p_team_id
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Team does not exist.';
    END IF;

    SELECT ROUND(
        100.0 * AVG(m.winner_team_id = p_team_id),
        2
    )
    INTO v_win_rate
    FROM matches AS m
    WHERE m.status = 'COMPLETED'
      AND (
          m.team_a_id = p_team_id
          OR m.team_b_id = p_team_id
      );

    RETURN v_win_rate;
END$$


-- Returns the team's game win rate as a percentage.
-- Includes completed games in ongoing or completed matches.
-- Games belonging to cancelled matches are excluded.
-- Returns NULL when no qualifying games exist.
CREATE FUNCTION fn_team_game_win_rate (
    p_team_id BIGINT UNSIGNED
)
RETURNS DECIMAL(5,2)
NOT DETERMINISTIC
READS SQL DATA
SQL SECURITY INVOKER
BEGIN
    DECLARE v_win_rate DECIMAL(5,2);

    IF NOT EXISTS (
        SELECT 1 FROM teams WHERE id = p_team_id
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Team does not exist.';
    END IF;

    SELECT ROUND(
        100.0 * AVG(g.winner_team_id = p_team_id),
        2
    )
    INTO v_win_rate
    FROM games AS g
    INNER JOIN matches AS m ON m.id = g.match_id
    WHERE g.winner_team_id IS NOT NULL
      AND g.ended_at IS NOT NULL
      AND m.status IN ('IN_PROGRESS', 'COMPLETED')
      AND (
          m.team_a_id = p_team_id
          OR m.team_b_id = p_team_id
      );

    RETURN v_win_rate;
END$$


-- ============================================================
-- 3. TEAM MEMBERSHIP PROCEDURE
-- ============================================================

-- Inserts a membership period or updates its end date.
-- Use the same team, player and joined_at to update a period.
--
-- Periods for the SAME team/player pair cannot overlap.
-- left_at is an exclusive endpoint:
-- a new period may begin exactly when the previous one ends.
--
-- Membership of different teams at the same time is not
-- prohibited by this model.
CREATE PROCEDURE sp_save_team_membership (
    IN p_team_id BIGINT UNSIGNED,
    IN p_player_id BIGINT UNSIGNED,
    IN p_joined_at DATETIME,
    IN p_left_at DATETIME
)
MODIFIES SQL DATA
SQL SECURITY INVOKER
BEGIN
    DECLARE v_player_id BIGINT UNSIGNED DEFAULT NULL;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Serializes membership changes for this player.
    SELECT id INTO v_player_id
    FROM players
    WHERE id = p_player_id
    FOR UPDATE;

    IF v_player_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Player does not exist.';
    END IF;

    IF p_joined_at IS NULL
       OR (
           p_left_at IS NOT NULL
           AND p_left_at <= p_joined_at
       )
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid membership dates.';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM team_players
        WHERE team_id = p_team_id
          AND player_id = p_player_id
          AND joined_at <> p_joined_at
          AND (p_left_at IS NULL OR joined_at < p_left_at)
          AND (left_at IS NULL OR left_at > p_joined_at)
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Membership periods overlap.';
    END IF;

    INSERT INTO team_players (
        team_id, player_id, joined_at, left_at
    )
    VALUES (
        p_team_id, p_player_id, p_joined_at, p_left_at
    )
    ON DUPLICATE KEY UPDATE
        left_at = p_left_at;

    COMMIT;
END$$


-- ============================================================
-- 4. GAME RESULT PROCEDURE
-- ============================================================

-- Creates or corrects a completed game result.
-- An existing game is identified by match_id + game_number.
--
-- Results must form a consecutive sequence: 1, 2, 3, ...
-- The procedure rejects:
--   - non-participating winners;
--   - invalid dates;
--   - cancelled matches;
--   - missing game numbers;
--   - games played after the series was already decided.
--
-- The game and match result are saved in one transaction.
CREATE PROCEDURE sp_record_game_result (
    IN p_match_id BIGINT UNSIGNED,
    IN p_game_number INT,
    IN p_winner_team_id BIGINT UNSIGNED,
    IN p_started_at DATETIME,
    IN p_ended_at DATETIME
)
MODIFIES SQL DATA
SQL SECURITY INVOKER
BEGIN
    DECLARE v_team_a BIGINT UNSIGNED DEFAULT NULL;
    DECLARE v_team_b BIGINT UNSIGNED;
    DECLARE v_format VARCHAR(3);
    DECLARE v_status VARCHAR(20);
    DECLARE v_max_games INT;
    DECLARE v_required_wins INT;

    DECLARE v_game_count INT;
    DECLARE v_finished_count INT;
    DECLARE v_last_game_number INT;
    DECLARE v_last_winner BIGINT UNSIGNED;

    DECLARE v_wins_a INT;
    DECLARE v_wins_b INT;
    DECLARE v_match_winner BIGINT UNSIGNED DEFAULT NULL;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Serializes result changes for this match.
    SELECT team_a_id, team_b_id, format, status
    INTO v_team_a, v_team_b, v_format, v_status
    FROM matches
    WHERE id = p_match_id
    FOR UPDATE;

    IF v_team_a IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Match does not exist.';
    END IF;

    IF v_status = 'CANCELLED' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot record results for a cancelled match.';
    END IF;

    IF p_winner_team_id IS NULL
       OR p_winner_team_id NOT IN (v_team_a, v_team_b)
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'The winner must participate in the match.';
    END IF;

    SET v_max_games = CASE v_format
        WHEN 'BO1' THEN 1
        WHEN 'BO3' THEN 3
        WHEN 'BO5' THEN 5
    END;

    SET v_required_wins = (v_max_games + 1) DIV 2;

    IF p_game_number IS NULL
       OR p_game_number < 1
       OR p_game_number > v_max_games
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Game number is invalid for this match format.';
    END IF;

    IF p_started_at IS NULL
       OR p_ended_at IS NULL
       OR p_ended_at < p_started_at
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Provide valid game start and end times.';
    END IF;

    INSERT INTO games (
        match_id,
        game_number,
        winner_team_id,
        started_at,
        ended_at
    )
    VALUES (
        p_match_id,
        p_game_number,
        p_winner_team_id,
        p_started_at,
        p_ended_at
    )
    ON DUPLICATE KEY UPDATE
        winner_team_id = p_winner_team_id,
        started_at = p_started_at,
        ended_at = p_ended_at;

    SELECT
        COUNT(*),
        COUNT(winner_team_id),
        MAX(game_number),
        COALESCE(SUM(winner_team_id = v_team_a), 0),
        COALESCE(SUM(winner_team_id = v_team_b), 0)
    INTO
        v_game_count,
        v_finished_count,
        v_last_game_number,
        v_wins_a,
        v_wins_b
    FROM games
    WHERE match_id = p_match_id;

    -- Unique positive game numbers must form a complete sequence.
    IF v_game_count <> v_last_game_number
       OR v_finished_count <> v_game_count
       OR v_game_count > v_max_games
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Record completed games consecutively, starting at 1.';
    END IF;

    IF v_wins_a + v_wins_b <> v_game_count THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'An existing game has an invalid winner.';
    END IF;

    IF v_wins_a > v_required_wins
       OR v_wins_b > v_required_wins
       OR (
           v_wins_a = v_required_wins
           AND v_wins_b = v_required_wins
       )
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'These results exceed the series win limit.';
    END IF;

    IF v_wins_a = v_required_wins THEN
        SET v_match_winner = v_team_a;
    ELSEIF v_wins_b = v_required_wins THEN
        SET v_match_winner = v_team_b;
    END IF;

    SELECT winner_team_id
    INTO v_last_winner
    FROM games
    WHERE match_id = p_match_id
      AND game_number = v_last_game_number;

    -- The deciding win must be the final recorded game.
    IF v_match_winner IS NOT NULL
       AND v_last_winner <> v_match_winner
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A game was recorded after the series was decided.';
    END IF;

    UPDATE matches
    SET
        winner_team_id = v_match_winner,
        status = CASE
            WHEN v_match_winner IS NULL THEN 'IN_PROGRESS'
            ELSE 'COMPLETED'
        END
    WHERE id = p_match_id;

    COMMIT;
END$$


-- ============================================================
-- 5. PLAYER STATISTICS PROCEDURE
-- ============================================================

-- Inserts or updates one player's statistics for one game.
-- team_id records the team represented in that specific game.
--
-- The procedure validates match participation.
-- It does not require a team_players membership record;
-- substitutes and incomplete roster history are allowed.
CREATE PROCEDURE sp_save_player_game_stats (
    IN p_game_id BIGINT UNSIGNED,
    IN p_player_id BIGINT UNSIGNED,
    IN p_team_id BIGINT UNSIGNED,
    IN p_champion_id BIGINT UNSIGNED,
    IN p_kills INT,
    IN p_deaths INT,
    IN p_assists INT
)
MODIFIES SQL DATA
SQL SECURITY INVOKER
BEGIN
    DECLARE v_team_a BIGINT UNSIGNED DEFAULT NULL;
    DECLARE v_team_b BIGINT UNSIGNED;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    SELECT m.team_a_id, m.team_b_id
    INTO v_team_a, v_team_b
    FROM games AS g
    INNER JOIN matches AS m ON m.id = g.match_id
    WHERE g.id = p_game_id
    FOR UPDATE;

    IF v_team_a IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Game does not exist.';
    END IF;

    IF p_team_id IS NULL
       OR p_team_id NOT IN (v_team_a, v_team_b)
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'The player team must participate in the match.';
    END IF;

    IF p_kills IS NULL OR p_kills < 0
       OR p_deaths IS NULL OR p_deaths < 0
       OR p_assists IS NULL OR p_assists < 0
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Statistics must be non-negative integers.';
    END IF;

    INSERT INTO player_game_stats (
        game_id,
        player_id,
        team_id,
        champion_id,
        kills,
        deaths,
        assists
    )
    VALUES (
        p_game_id,
        p_player_id,
        p_team_id,
        p_champion_id,
        p_kills,
        p_deaths,
        p_assists
    )
    ON DUPLICATE KEY UPDATE
        team_id = p_team_id,
        champion_id = p_champion_id,
        kills = p_kills,
        deaths = p_deaths,
        assists = p_assists;

    COMMIT;
END$$


-- ============================================================
-- 6. TOURNAMENT STANDINGS PROCEDURE
-- ============================================================

-- Returns every registered team, including teams with no results.
-- Counts completed matches in the requested tournament only.
-- This is a results overview, not a tournament-specific
-- points or tie-break system.
CREATE PROCEDURE sp_get_tournament_standings (
    IN p_tournament_id BIGINT UNSIGNED
)
READS SQL DATA
SQL SECURITY INVOKER
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM tournaments
        WHERE id = p_tournament_id
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Tournament does not exist.';
    END IF;

    SELECT
        t.id AS team_id,
        t.name AS team_name,
        COUNT(m.id) AS matches_played,
        COALESCE(SUM(m.winner_team_id = t.id), 0) AS wins,
        COUNT(m.id)
            - COALESCE(SUM(m.winner_team_id = t.id), 0) AS losses,
        ROUND(
            100.0 * COALESCE(SUM(m.winner_team_id = t.id), 0)
            / NULLIF(COUNT(m.id), 0),
            2
        ) AS match_win_rate
    FROM tournament_teams AS tt
    INNER JOIN teams AS t
        ON t.id = tt.team_id
    LEFT JOIN matches AS m
        ON m.tournament_id = tt.tournament_id
       AND m.status = 'COMPLETED'
       AND (m.team_a_id = t.id OR m.team_b_id = t.id)
    WHERE tt.tournament_id = p_tournament_id
    GROUP BY t.id, t.name
    ORDER BY wins DESC, match_win_rate DESC, t.name, t.id;
END$$

DELIMITER ;