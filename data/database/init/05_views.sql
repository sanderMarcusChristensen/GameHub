USE gameHub;


-- ============================================================
-- 1. PLAYER LEADERBOARD
-- ============================================================

-- Combines player identity with the latest calculated
-- aggregate statistics for the application leaderboard.
CREATE OR REPLACE VIEW vw_player_leaderboard AS
SELECT
    p.id AS player_id,
    p.nickname,
    ps.games_played,
    ps.wins,
    ps.losses,
    ps.win_rate,
    ps.updated_at
FROM players AS p
INNER JOIN player_statistics AS ps
    ON ps.player_id = p.id;


-- ============================================================
-- 2. MATCH OVERVIEW
-- ============================================================

-- Provides a readable match overview by combining the match
-- with its tournament, participating teams, and winner.
CREATE OR REPLACE VIEW vw_match_overview AS
SELECT
    m.id AS match_id,
    t.name AS tournament_name,
    ta.name AS team_a,
    tb.name AS team_b,
    m.format,
    m.status,
    w.name AS winner,
    m.scheduled_at
FROM matches AS m
INNER JOIN tournaments AS t
    ON t.id = m.tournament_id
INNER JOIN teams AS ta
    ON ta.id = m.team_a_id
INNER JOIN teams AS tb
    ON tb.id = m.team_b_id
LEFT JOIN teams AS w
    ON w.id = m.winner_team_id;