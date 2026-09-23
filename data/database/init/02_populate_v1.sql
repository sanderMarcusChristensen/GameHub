USE gamehub;

-- =========================================================
-- CHAMPIONS
-- =========================================================

INSERT INTO champions
    (name, role, attack_range, release_date)
VALUES
    ('Ahri', 'Mid', 550, '2011-12-14'),
    ('Lee Sin', 'Jungle', 125, '2011-04-01'),
    ('Jinx', 'ADC', 525, '2013-10-10'),
    ('Thresh', 'Support', 450, '2013-01-23'),
    ('Garen', 'Top', 175, '2010-04-27'),
    ('Orianna', 'Mid', 525, '2011-06-01'),
    ('Viego', 'Jungle', 200, '2021-01-21'),
    ('Nautilus', 'Support', 175, '2012-02-14');

-- =========================================================
-- PLAYERS
-- =========================================================

INSERT INTO players
    (name, ign, nationality, date_of_birth)
VALUES
    ('Martin Hansen', 'Frost', 'Denmark', '2001-05-12'),
    ('Jonas Nielsen', 'Shadow', 'Sweden', '2002-08-21'),
    ('Emil Larsen', 'Blaze', 'Denmark', '2000-11-03'),
    ('Oliver Jensen', 'Ace', 'Norway', '2003-02-17'),
    ('Lucas Schmidt', 'Lynx', 'Germany', '2001-07-29'),
    ('Noah Anderson', 'Nova', 'United Kingdom', '2002-04-11'),
    ('William Brown', 'Wolf', 'United States', '1999-12-05'),
    ('Elias Müller', 'Storm', 'Germany', '2000-09-18'),
    ('Oscar Berg', 'Raven', 'Sweden', '2003-01-30'),
    ('Mikkel Sørensen', 'Titan', 'Denmark', '1998-06-22');

-- =========================================================
-- TEAMS
-- =========================================================

INSERT INTO teams
    (name, short_name)
VALUES
    ('Copenhagen Wolves', 'CW'),
    ('Nordic Titans', 'NT'),
    ('Berlin Bears', 'BB'),
    ('London Lions', 'LL');

-- =========================================================
-- PLAYER / TEAM RELATIONSHIPS
-- =========================================================

INSERT INTO player_teams
    (player_id, team_id, joined_at, left_at)
VALUES
    (1, 1, '2025-01-01', NULL),
    (2, 1, '2025-02-01', NULL),
    (3, 2, '2025-01-15', NULL),
    (4, 2, '2025-03-01', NULL),
    (5, 3, '2025-01-01', NULL),
    (6, 3, '2025-01-01', NULL),
    (7, 4, '2025-02-15', NULL),
    (8, 4, '2025-02-15', NULL),

    -- Tidligere medlemskab
    (9, 1, '2024-01-01', '2024-12-31'),
    (9, 3, '2025-01-01', NULL),

    (10, 2, '2024-06-01', '2024-12-31'),
    (10, 4, '2025-01-01', NULL);

-- =========================================================
-- PLAYER STATS
-- =========================================================

INSERT INTO player_stats
    (
        player_id,
        games_played,
        wins,
        losses,
        win_rate,
        avg_kills,
        avg_deaths,
        avg_assists,
        source,
        sample_scope,
        fetched_at,
        is_synthetic
    )
VALUES
    (1, 50, 32, 18, 0.640000, 7.20, 3.10, 6.80,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (2, 50, 28, 22, 0.560000, 6.40, 4.20, 7.10,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (3, 45, 29, 16, 0.644444, 8.10, 3.80, 5.20,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (4, 40, 21, 19, 0.525000, 5.90, 4.50, 8.30,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (5, 55, 34, 21, 0.618182, 6.70, 3.90, 7.40,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (6, 48, 25, 23, 0.520833, 5.80, 4.60, 9.10,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (7, 52, 31, 21, 0.596154, 7.60, 4.00, 6.20,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (8, 47, 27, 20, 0.574468, 6.10, 4.30, 8.00,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (9, 43, 24, 19, 0.558140, 6.30, 4.10, 7.70,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE),

    (10, 50, 30, 20, 0.600000, 7.00, 3.70, 6.90,
        'dummy', 'Last 50 games', UTC_TIMESTAMP(), TRUE);

-- =========================================================
-- PLAYER / CHAMPION STATS
-- =========================================================

INSERT INTO player_champion_stats
    (
        player_id,
        champion_id,
        games_played,
        wins,
        losses,
        win_rate,
        avg_kills,
        avg_deaths,
        avg_assists,
        source,
        sample_scope,
        fetched_at,
        is_synthetic
    )
VALUES
    (1, 1, 15, 10, 5, 0.666667, 7.80, 2.90, 6.40,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (1, 2, 10, 6, 4, 0.600000, 6.20, 3.50, 8.10,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (2, 3, 20, 12, 8, 0.600000, 8.40, 3.70, 5.90,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (3, 5, 12, 8, 4, 0.666667, 7.10, 3.20, 5.00,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (4, 4, 15, 8, 7, 0.533333, 2.10, 4.40, 11.20,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (5, 6, 18, 11, 7, 0.611111, 7.00, 3.80, 7.20,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (6, 8, 14, 8, 6, 0.571429, 2.40, 4.10, 12.00,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (7, 7, 16, 10, 6, 0.625000, 7.30, 3.90, 6.50,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (8, 2, 13, 7, 6, 0.538462, 6.80, 4.20, 7.60,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (9, 1, 11, 6, 5, 0.545455, 6.50, 4.00, 7.10,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE),

    (10, 3, 17, 11, 6, 0.647059, 8.00, 3.60, 6.00,
        'dummy', 'Champion stats', UTC_TIMESTAMP(), TRUE);

-- =========================================================
-- MATCHES
-- =========================================================

INSERT INTO matches
    (
        team1_id,
        team2_id,
        winner_team_id,
        played_at,
        source,
        external_id,
        is_synthetic
    )
VALUES
    (1, 2, 1, '2026-09-01 18:00:00', 'dummy', 'DUMMY-001', TRUE),
    (3, 4, 4, '2026-09-02 18:00:00', 'dummy', 'DUMMY-002', TRUE),
    (1, 3, 3, '2026-09-03 19:00:00', 'dummy', 'DUMMY-003', TRUE),
    (2, 4, 2, '2026-09-04 19:00:00', 'dummy', 'DUMMY-004', TRUE),
    (1, 4, 1, '2026-09-05 18:00:00', 'dummy', 'DUMMY-005', TRUE),
    (2, 3, 3, '2026-09-06 18:00:00', 'dummy', 'DUMMY-006', TRUE);

-- =========================================================
-- MATCH PARTICIPANTS
-- =========================================================

-- Match 1
-- Copenhagen Wolves (team 1) vs Nordic Titans (team 2)

INSERT INTO match_participants
    (match_id, player_id, champion_id, team_side, kills, deaths, assists)
VALUES
    (1, 1, 1, 1, 8, 2, 7),
    (1, 2, 3, 1, 6, 3, 9),
    (1, 3, 5, 2, 4, 5, 3),
    (1, 4, 4, 2, 2, 6, 10);

-- Match 2
-- Berlin Bears vs London Lions

INSERT INTO match_participants
    (match_id, player_id, champion_id, team_side, kills, deaths, assists)
VALUES
    (2, 5, 6, 1, 7, 4, 6),
    (2, 6, 8, 1, 1, 7, 13),
    (2, 7, 7, 2, 9, 3, 5),
    (2, 8, 2, 2, 6, 4, 8);

-- Match 3
-- Copenhagen Wolves vs Berlin Bears

INSERT INTO match_participants
    (match_id, player_id, champion_id, team_side, kills, deaths, assists)
VALUES
    (3, 1, 1, 1, 5, 4, 8),
    (3, 2, 3, 1, 7, 5, 6),
    (3, 5, 6, 2, 10, 2, 7),
    (3, 6, 8, 2, 3, 8, 15);

-- Match 4
-- Nordic Titans vs London Lions

INSERT INTO match_participants
    (match_id, player_id, champion_id, team_side, kills, deaths, assists)
VALUES
    (4, 3, 5, 1, 9, 3, 4),
    (4, 4, 4, 1, 2, 5, 14),
    (4, 7, 7, 2, 6, 6, 5),
    (4, 8, 2, 2, 5, 5, 9);

-- Match 5
-- Copenhagen Wolves vs London Lions

INSERT INTO match_participants
    (match_id, player_id, champion_id, team_side, kills, deaths, assists)
VALUES
    (5, 1, 1, 1, 11, 2, 5),
    (5, 2, 3, 1, 8, 3, 7),
    (5, 7, 7, 2, 4, 8, 6),
    (5, 8, 2, 2, 3, 9, 11);

-- Match 6
-- Nordic Titans vs Berlin Bears

INSERT INTO match_participants
    (match_id, player_id, champion_id, team_side, kills, deaths, assists)
VALUES
    (6, 3, 5, 1, 3, 7, 8),
    (6, 4, 4, 1, 1, 6, 12),
    (6, 5, 6, 2, 8, 2, 9),
    (6, 6, 8, 2, 2, 4, 16);