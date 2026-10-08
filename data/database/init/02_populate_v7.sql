-- ============================================================
-- GameHub sample data
--
-- Run once after 01_create.sql in an unpopulated database.
-- Run the entire script in the same connection.
-- Requires autocommit enabled and no active transaction.
--
-- Players and match results are fictional.
-- Champion names are actual League of Legends champions.
-- Champion release dates are retained from the original seed.
--
-- Six BO1 matches, with one completed game per match.
-- Statistics cover four players per game, not full 5v5 rosters.
-- Game durations are synthetic: 35 minutes.
--
-- This script is not intended to be run repeatedly.
-- ============================================================

USE gameHub;


-- ============================================================
-- 1. PLAYERS: FICTIONAL PLAYER NICKNAMES
-- ============================================================

INSERT INTO players (nickname) VALUES ('Frost');
SET @seed_frost = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Shadow');
SET @seed_shadow = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Blaze');
SET @seed_blaze = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Ace');
SET @seed_ace = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Lynx');
SET @seed_lynx = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Nova');
SET @seed_nova = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Wolf');
SET @seed_wolf = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Storm');
SET @seed_storm = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('Raven');
SET @seed_raven = LAST_INSERT_ID();

INSERT INTO players (nickname) VALUES ('PlayerTen');
SET @seed_player_ten = LAST_INSERT_ID();


-- ============================================================
-- 2. TEAMS
-- ============================================================

INSERT INTO teams (name, short_name)
VALUES ('Copenhagen Wolves', 'CW');
SET @seed_cw = LAST_INSERT_ID();

INSERT INTO teams (name, short_name)
VALUES ('Nordic Titans', 'NT');
SET @seed_nt = LAST_INSERT_ID();

INSERT INTO teams (name, short_name)
VALUES ('Berlin Bears', 'BB');
SET @seed_bb = LAST_INSERT_ID();

INSERT INTO teams (name, short_name)
VALUES ('London Lions', 'LL');
SET @seed_ll = LAST_INSERT_ID();


-- ============================================================
-- 3. CHAMPIONS: ACTUAL LEAGUE OF LEGENDS CHARACTERS
-- ============================================================

INSERT INTO champions (name, released_at)
VALUES ('Ahri', '2011-12-14');
SET @seed_ahri = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Lee Sin', '2011-04-01');
SET @seed_lee_sin = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Jinx', '2013-10-10');
SET @seed_jinx = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Thresh', '2013-01-23');
SET @seed_thresh = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Garen', '2010-04-27');
SET @seed_garen = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Orianna', '2011-06-01');
SET @seed_orianna = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Viego', '2021-01-21');
SET @seed_viego = LAST_INSERT_ID();

INSERT INTO champions (name, released_at)
VALUES ('Nautilus', '2012-02-14');
SET @seed_nautilus = LAST_INSERT_ID();


-- ============================================================
-- 4. CHAMPION ROLES
-- Example position assignments, not an exhaustive role list.
-- ============================================================

INSERT INTO champion_roles (name) VALUES ('Top');
SET @seed_top = LAST_INSERT_ID();

INSERT INTO champion_roles (name) VALUES ('Jungle');
SET @seed_jungle = LAST_INSERT_ID();

INSERT INTO champion_roles (name) VALUES ('Mid');
SET @seed_mid = LAST_INSERT_ID();

INSERT INTO champion_roles (name) VALUES ('ADC');
SET @seed_adc = LAST_INSERT_ID();

INSERT INTO champion_roles (name) VALUES ('Support');
SET @seed_support = LAST_INSERT_ID();

INSERT INTO champions_has_champion_roles (
    champions_id,
    champion_roles_id
)
VALUES
    (@seed_ahri,     @seed_mid),
    (@seed_lee_sin,  @seed_jungle),
    (@seed_jinx,     @seed_adc),
    (@seed_thresh,  @seed_support),
    (@seed_garen,    @seed_top),
    (@seed_orianna,  @seed_mid),
    (@seed_viego,    @seed_jungle),
    (@seed_nautilus, @seed_support);


-- ============================================================
-- 5. TEAM MEMBERSHIP HISTORY
-- Parameters: team, player, joined_at, left_at.
-- ============================================================

CALL sp_save_team_membership(
    @seed_cw, @seed_frost, '2025-01-01 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_cw, @seed_shadow, '2025-02-01 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_nt, @seed_blaze, '2025-01-15 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_nt, @seed_ace, '2025-03-01 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_bb, @seed_lynx, '2025-01-01 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_bb, @seed_nova, '2025-01-01 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_ll, @seed_wolf, '2025-02-15 00:00:00', NULL
);

CALL sp_save_team_membership(
    @seed_ll, @seed_storm, '2025-02-15 00:00:00', NULL
);

-- Raven: previous team, then current team.
CALL sp_save_team_membership(
    @seed_cw, @seed_raven,
    '2024-01-01 00:00:00', '2024-12-31 23:59:59'
);

CALL sp_save_team_membership(
    @seed_bb, @seed_raven, '2025-01-01 00:00:00', NULL
);

-- PlayerTen: previous team, then current team.
CALL sp_save_team_membership(
    @seed_nt, @seed_player_ten,
    '2024-06-01 00:00:00', '2024-12-31 23:59:59'
);

CALL sp_save_team_membership(
    @seed_ll, @seed_player_ten, '2025-01-01 00:00:00', NULL
);


-- ============================================================
-- 6. TOURNAMENT AND REGISTERED TEAMS
-- ============================================================

INSERT INTO tournaments (name, start_date, end_date)
VALUES (
    'GameHub Dummy Tournament',
    '2026-09-01 00:00:00',
    '2026-09-06 23:59:59'
);
SET @seed_tournament = LAST_INSERT_ID();

INSERT INTO tournament_teams (
    tournament_id, team_id, registered_at
)
VALUES
    (@seed_tournament, @seed_cw, '2026-08-25 12:00:00'),
    (@seed_tournament, @seed_nt, '2026-08-25 12:00:00'),
    (@seed_tournament, @seed_bb, '2026-08-25 12:00:00'),
    (@seed_tournament, @seed_ll, '2026-08-25 12:00:00');


-- ============================================================
-- 7. MATCHES
-- Start as SCHEDULED with no winner.
-- The result procedure will complete each BO1.
-- ============================================================

INSERT INTO matches (
    tournament_id, team_a_id, team_b_id, format, scheduled_at
)
VALUES (
    @seed_tournament, @seed_cw, @seed_nt,
    'BO1', '2026-09-01 18:00:00'
);
SET @seed_match_1 = LAST_INSERT_ID();

INSERT INTO matches (
    tournament_id, team_a_id, team_b_id, format, scheduled_at
)
VALUES (
    @seed_tournament, @seed_bb, @seed_ll,
    'BO1', '2026-09-02 18:00:00'
);
SET @seed_match_2 = LAST_INSERT_ID();

INSERT INTO matches (
    tournament_id, team_a_id, team_b_id, format, scheduled_at
)
VALUES (
    @seed_tournament, @seed_cw, @seed_bb,
    'BO1', '2026-09-03 19:00:00'
);
SET @seed_match_3 = LAST_INSERT_ID();

INSERT INTO matches (
    tournament_id, team_a_id, team_b_id, format, scheduled_at
)
VALUES (
    @seed_tournament, @seed_nt, @seed_ll,
    'BO1', '2026-09-04 19:00:00'
);
SET @seed_match_4 = LAST_INSERT_ID();

INSERT INTO matches (
    tournament_id, team_a_id, team_b_id, format, scheduled_at
)
VALUES (
    @seed_tournament, @seed_cw, @seed_ll,
    'BO1', '2026-09-05 18:00:00'
);
SET @seed_match_5 = LAST_INSERT_ID();

INSERT INTO matches (
    tournament_id, team_a_id, team_b_id, format, scheduled_at
)
VALUES (
    @seed_tournament, @seed_nt, @seed_bb,
    'BO1', '2026-09-06 18:00:00'
);
SET @seed_match_6 = LAST_INSERT_ID();


-- ============================================================
-- 8. COMPLETED GAME RESULTS
-- Parameters: match, game number, winner, start, end.
-- ============================================================

CALL sp_record_game_result(
    @seed_match_1, 1, @seed_cw,
    '2026-09-01 18:00:00', '2026-09-01 18:35:00'
);

CALL sp_record_game_result(
    @seed_match_2, 1, @seed_ll,
    '2026-09-02 18:00:00', '2026-09-02 18:35:00'
);

CALL sp_record_game_result(
    @seed_match_3, 1, @seed_bb,
    '2026-09-03 19:00:00', '2026-09-03 19:35:00'
);

CALL sp_record_game_result(
    @seed_match_4, 1, @seed_nt,
    '2026-09-04 19:00:00', '2026-09-04 19:35:00'
);

CALL sp_record_game_result(
    @seed_match_5, 1, @seed_cw,
    '2026-09-05 18:00:00', '2026-09-05 18:35:00'
);

CALL sp_record_game_result(
    @seed_match_6, 1, @seed_bb,
    '2026-09-06 18:00:00', '2026-09-06 18:35:00'
);


-- ============================================================
-- 9. FIND THE GENERATED GAME IDs
-- ============================================================

SET @seed_game_1 = (
    SELECT id FROM games
    WHERE match_id = @seed_match_1 AND game_number = 1
);

SET @seed_game_2 = (
    SELECT id FROM games
    WHERE match_id = @seed_match_2 AND game_number = 1
);

SET @seed_game_3 = (
    SELECT id FROM games
    WHERE match_id = @seed_match_3 AND game_number = 1
);

SET @seed_game_4 = (
    SELECT id FROM games
    WHERE match_id = @seed_match_4 AND game_number = 1
);

SET @seed_game_5 = (
    SELECT id FROM games
    WHERE match_id = @seed_match_5 AND game_number = 1
);

SET @seed_game_6 = (
    SELECT id FROM games
    WHERE match_id = @seed_match_6 AND game_number = 1
);


-- ============================================================
-- 10. PLAYER STATISTICS
-- Parameters: game, player, team, champion, kills, deaths, assists.
-- ============================================================

-- Game 1: Copenhagen Wolves vs Nordic Titans.
CALL sp_save_player_game_stats(
    @seed_game_1, @seed_frost, @seed_cw, @seed_ahri, 8, 2, 7
);
CALL sp_save_player_game_stats(
    @seed_game_1, @seed_shadow, @seed_cw, @seed_jinx, 6, 3, 9
);
CALL sp_save_player_game_stats(
    @seed_game_1, @seed_blaze, @seed_nt, @seed_garen, 4, 5, 3
);
CALL sp_save_player_game_stats(
    @seed_game_1, @seed_ace, @seed_nt, @seed_thresh, 2, 6, 10
);

-- Game 2: Berlin Bears vs London Lions.
CALL sp_save_player_game_stats(
    @seed_game_2, @seed_lynx, @seed_bb, @seed_orianna, 7, 4, 6
);
CALL sp_save_player_game_stats(
    @seed_game_2, @seed_nova, @seed_bb, @seed_nautilus, 1, 7, 13
);
CALL sp_save_player_game_stats(
    @seed_game_2, @seed_wolf, @seed_ll, @seed_viego, 9, 3, 5
);
CALL sp_save_player_game_stats(
    @seed_game_2, @seed_storm, @seed_ll, @seed_lee_sin, 6, 4, 8
);

-- Game 3: Copenhagen Wolves vs Berlin Bears.
CALL sp_save_player_game_stats(
    @seed_game_3, @seed_frost, @seed_cw, @seed_ahri, 5, 4, 8
);
CALL sp_save_player_game_stats(
    @seed_game_3, @seed_shadow, @seed_cw, @seed_jinx, 7, 5, 6
);
CALL sp_save_player_game_stats(
    @seed_game_3, @seed_lynx, @seed_bb, @seed_orianna, 10, 2, 7
);
CALL sp_save_player_game_stats(
    @seed_game_3, @seed_nova, @seed_bb, @seed_nautilus, 3, 8, 15
);

-- Game 4: Nordic Titans vs London Lions.
CALL sp_save_player_game_stats(
    @seed_game_4, @seed_blaze, @seed_nt, @seed_garen, 9, 3, 4
);
CALL sp_save_player_game_stats(
    @seed_game_4, @seed_ace, @seed_nt, @seed_thresh, 2, 5, 14
);
CALL sp_save_player_game_stats(
    @seed_game_4, @seed_wolf, @seed_ll, @seed_viego, 6, 6, 5
);
CALL sp_save_player_game_stats(
    @seed_game_4, @seed_storm, @seed_ll, @seed_lee_sin, 5, 5, 9
);

-- Game 5: Copenhagen Wolves vs London Lions.
CALL sp_save_player_game_stats(
    @seed_game_5, @seed_frost, @seed_cw, @seed_ahri, 11, 2, 5
);
CALL sp_save_player_game_stats(
    @seed_game_5, @seed_shadow, @seed_cw, @seed_jinx, 8, 3, 7
);
CALL sp_save_player_game_stats(
    @seed_game_5, @seed_wolf, @seed_ll, @seed_viego, 4, 8, 6
);
CALL sp_save_player_game_stats(
    @seed_game_5, @seed_storm, @seed_ll, @seed_lee_sin, 3, 9, 11
);

-- Game 6: Nordic Titans vs Berlin Bears.
CALL sp_save_player_game_stats(
    @seed_game_6, @seed_blaze, @seed_nt, @seed_garen, 3, 7, 8
);
CALL sp_save_player_game_stats(
    @seed_game_6, @seed_ace, @seed_nt, @seed_thresh, 1, 6, 12
);
CALL sp_save_player_game_stats(
    @seed_game_6, @seed_lynx, @seed_bb, @seed_orianna, 8, 2, 9
);
CALL sp_save_player_game_stats(
    @seed_game_6, @seed_nova, @seed_bb, @seed_nautilus, 2, 4, 16
);


-- ============================================================
-- 11. CHECK ROW COUNTS
-- Expected totals assume an initially empty database.
-- ============================================================

SELECT
    table_name,
    actual_rows,
    expected_rows,
    CASE
        WHEN actual_rows = expected_rows THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result
FROM (
    SELECT 'players' AS table_name,
           COUNT(*) AS actual_rows, 10 AS expected_rows
    FROM players

    UNION ALL
    SELECT 'teams', COUNT(*), 4 FROM teams

    UNION ALL
    SELECT 'champions', COUNT(*), 8 FROM champions

    UNION ALL
    SELECT 'champion_roles', COUNT(*), 5 FROM champion_roles

    UNION ALL
    SELECT 'champions_has_champion_roles', COUNT(*), 8
    FROM champions_has_champion_roles

    UNION ALL
    SELECT 'team_players', COUNT(*), 12 FROM team_players

    UNION ALL
    SELECT 'tournaments', COUNT(*), 1 FROM tournaments

    UNION ALL
    SELECT 'tournament_teams', COUNT(*), 4 FROM tournament_teams

    UNION ALL
    SELECT 'matches', COUNT(*), 6 FROM matches

    UNION ALL
    SELECT 'games', COUNT(*), 6 FROM games

    UNION ALL
    SELECT 'player_game_stats', COUNT(*), 24 FROM player_game_stats
) AS counts;


-- ============================================================
-- 12. CHECK RESULTS AND WIN RATES
-- ============================================================

SELECT
    m.id AS match_id,
    a.name AS team_a,
    b.name AS team_b,
    m.status,
    w.name AS winner
FROM matches AS m
JOIN teams AS a ON a.id = m.team_a_id
JOIN teams AS b ON b.id = m.team_b_id
LEFT JOIN teams AS w ON w.id = m.winner_team_id
WHERE m.tournament_id = @seed_tournament
ORDER BY m.scheduled_at;

SELECT
    name AS team_name,
    fn_team_match_win_rate(id) AS match_win_rate,
    fn_team_game_win_rate(id) AS game_win_rate
FROM teams
WHERE id IN (@seed_cw, @seed_nt, @seed_bb, @seed_ll)
ORDER BY name;

CALL sp_get_tournament_standings(@seed_tournament);