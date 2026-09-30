USE gamehub;

-- Hvilke champions spillede en bestemt spiller i går (UTC)?
-- Erstat 1 med spillerens faktiske id; ign behøver ikke være unikt.
SET @selected_player_id = 1;
SET @from_utc = UTC_DATE() - INTERVAL 1 DAY;
SET @to_utc = UTC_DATE();

SELECT
    p.ign,
    m.id AS match_id,
    m.played_at,
    c.name AS champion,
    t.name AS team,
    mp.kills,
    mp.deaths,
    mp.assists,
    m.source,
    m.is_synthetic
FROM match_participants mp
JOIN players p ON p.id = mp.player_id
JOIN champions c ON c.id = mp.champion_id
JOIN matches m ON m.id = mp.match_id
JOIN teams t ON t.id = CASE mp.team_side
    WHEN 1 THEN m.team1_id ELSE m.team2_id END
WHERE mp.player_id = @selected_player_id
  AND m.played_at >= @from_utc
  AND m.played_at < @to_utc
ORDER BY m.played_at, m.id;

-- Spillerens gemte champion-statistik (ikke beregnet fra lokale kampe).
SELECT p.ign, c.name AS champion, s.games_played, s.wins, s.losses,
       ROUND(s.win_rate * 100, 2) AS win_rate_percent,
       s.avg_kills, s.avg_deaths, s.avg_assists,
       s.source, s.sample_scope, s.fetched_at, s.is_synthetic
FROM player_champion_stats s
JOIN players p ON p.id = s.player_id
JOIN champions c ON c.id = s.champion_id
WHERE s.player_id = @selected_player_id
ORDER BY s.games_played DESC;
