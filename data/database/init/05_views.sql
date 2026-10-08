USE gameHub;

-- A simple leaderboard for the application to read.
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