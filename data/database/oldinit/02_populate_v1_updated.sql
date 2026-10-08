USE lol_esports;

-- =========================================================
-- PLAYERS
-- ERD_v6 stores nickname only. The previous name/nationality/
-- date_of_birth fields are therefore intentionally not carried over.
-- =========================================================

INSERT INTO players (ign)
VALUES
    ('Frost'),
    ('Shadow'),
    ('Blaze'),
    ('Ace'),
    ('Lynx'),
    ('Nova'),
    ('Wolf'),
    ('Storm'),
    ('Raven'),
    ('Titan');

-- =========================================================
-- TEAMS
-- =========================================================

INSERT INTO teams (name, short_name)
VALUES
    ('Copenhagen Wolves', 'CW'),
    ('Nordic Titans', 'NT'),
    ('Berlin Bears', 'BB'),
    ('London Lions', 'LL');

-- =========================================================
-- CHAMPIONS
-- =========================================================

INSERT INTO champions (name, released_at)
VALUES
    ('Ahri', '2011-12-14'),
    ('Lee Sin', '2011-04-01'),
    ('Jinx', '2013-10-10'),
    ('Thresh', '2013-01-23'),
    ('Garen', '2010-04-27'),
    ('Orianna', '2011-06-01'),
    ('Viego', '2021-01-21'),
    ('Nautilus', '2012-02-14');

-- =========================================================
-- CHAMPION ROLES
-- =========================================================

INSERT INTO champion_roles (id, name)
VALUES
    (1, 'Top'),
    (2, 'Jungle'),
    (3, 'Mid'),
    (4, 'ADC'),
    (5, 'Support');

INSERT INTO champions_has_champion_roles (champions_id, champion_roles_id)
VALUES
    (1, 3), -- Ahri: Mid
    (2, 2), -- Lee Sin: Jungle
    (3, 4), -- Jinx: ADC
    (4, 5), -- Thresh: Support
    (5, 1), -- Garen: Top
    (6, 3), -- Orianna: Mid
    (7, 2), -- Viego: Jungle
    (8, 5); -- Nautilus: Support

-- =========================================================
-- TEAM / PLAYER RELATIONSHIPS
-- =========================================================

INSERT INTO team_players (team_id, player_id, joined_at, left_at)
VALUES
    (1, 1, '2025-01-01 00:00:00', NULL),
    (1, 2, '2025-02-01 00:00:00', NULL),
    (2, 3, '2025-01-15 00:00:00', NULL),
    (2, 4, '2025-03-01 00:00:00', NULL),
    (3, 5, '2025-01-01 00:00:00', NULL),
    (3, 6, '2025-01-01 00:00:00', NULL),
    (4, 7, '2025-02-15 00:00:00', NULL),
    (4, 8, '2025-02-15 00:00:00', NULL),
    (1, 9, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
    (3, 9, '2025-01-01 00:00:00', NULL),
    (2, 10, '2024-06-01 00:00:00', '2024-12-31 23:59:59'),
    (4, 10, '2025-01-01 00:00:00', NULL);

-- =========================================================
-- TOURNAMENT
-- The previous v1 matches had no tournament. A single dummy
-- tournament is used so the old match data can be represented
-- in the new required relationship.
-- =========================================================

INSERT INTO tournaments (name, start_date, end_date)
VALUES
    ('GameHub Dummy Tournament', '2026-09-01 00:00:00', '2026-09-06 23:59:59');

INSERT INTO tournament_teams (tournament_id, team_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4);

-- =========================================================
-- MATCHES
-- Each old v1 match represented one game, so each is converted
-- to a BO1 completed match.
-- =========================================================

INSERT INTO matches
    (tournament_id, team_a_id, team_b_id, format, status, winner_team_id, scheduled_at)
VALUES
    (1, 1, 2, 'BO1', 'COMPLETED', 1, '2026-09-01 18:00:00'),
    (1, 3, 4, 'BO1', 'COMPLETED', 4, '2026-09-02 18:00:00'),
    (1, 1, 3, 'BO1', 'COMPLETED', 3, '2026-09-03 19:00:00'),
    (1, 2, 4, 'BO1', 'COMPLETED', 2, '2026-09-04 19:00:00'),
    (1, 1, 4, 'BO1', 'COMPLETED', 1, '2026-09-05 18:00:00'),
    (1, 2, 3, 'BO1', 'COMPLETED', 3, '2026-09-06 18:00:00');

-- =========================================================
-- GAMES
-- Each old match becomes game 1 of its corresponding BO1.
-- =========================================================

INSERT INTO games
    (match_id, game_number, winner_team_id, started_at, ended_at)
VALUES
    (1, 1, 1, '2026-09-01 18:00:00', NULL),
    (2, 1, 4, '2026-09-02 18:00:00', NULL),
    (3, 1, 3, '2026-09-03 19:00:00', NULL),
    (4, 1, 2, '2026-09-04 19:00:00', NULL),
    (5, 1, 1, '2026-09-05 18:00:00', NULL),
    (6, 1, 3, '2026-09-06 18:00:00', NULL);

-- =========================================================
-- PLAYER GAME STATS
-- Converted from the old match_participants data.
-- Note: the old dummy data contains four participants per game,
-- not five, so no additional participants have been invented.
-- =========================================================

-- Game 1: Copenhagen Wolves vs Nordic Titans
INSERT INTO player_game_stats
    (game_id, player_id, team_id, champion_id, kills, deaths, assists)
VALUES
    (1, 1, 1, 1, 8, 2, 7),
    (1, 2, 1, 3, 6, 3, 9),
    (1, 3, 2, 5, 4, 5, 3),
    (1, 4, 2, 4, 2, 6, 10);

-- Game 2: Berlin Bears vs London Lions
INSERT INTO player_game_stats
    (game_id, player_id, team_id, champion_id, kills, deaths, assists)
VALUES
    (2, 5, 3, 6, 7, 4, 6),
    (2, 6, 3, 8, 1, 7, 13),
    (2, 7, 4, 7, 9, 3, 5),
    (2, 8, 4, 2, 6, 4, 8);

-- Game 3: Copenhagen Wolves vs Berlin Bears
INSERT INTO player_game_stats
    (game_id, player_id, team_id, champion_id, kills, deaths, assists)
VALUES
    (3, 1, 1, 1, 5, 4, 8),
    (3, 2, 1, 3, 7, 5, 6),
    (3, 5, 3, 6, 10, 2, 7),
    (3, 6, 3, 8, 3, 8, 15);

-- Game 4: Nordic Titans vs London Lions
INSERT INTO player_game_stats
    (game_id, player_id, team_id, champion_id, kills, deaths, assists)
VALUES
    (4, 3, 2, 5, 9, 3, 4),
    (4, 4, 2, 4, 2, 5, 14),
    (4, 7, 4, 7, 6, 6, 5),
    (4, 8, 4, 2, 5, 5, 9);

-- Game 5: Copenhagen Wolves vs London Lions
INSERT INTO player_game_stats
    (game_id, player_id, team_id, champion_id, kills, deaths, assists)
VALUES
    (5, 1, 1, 1, 11, 2, 5),
    (5, 2, 1, 3, 8, 3, 7),
    (5, 7, 4, 7, 4, 8, 6),
    (5, 8, 4, 2, 3, 9, 11);

-- Game 6: Nordic Titans vs Berlin Bears
INSERT INTO player_game_stats
    (game_id, player_id, team_id, champion_id, kills, deaths, assists)
VALUES
    (6, 3, 2, 5, 3, 7, 8),
    (6, 4, 2, 4, 1, 6, 12),
    (6, 5, 3, 6, 8, 2, 9),
    (6, 6, 3, 8, 2, 4, 16);
