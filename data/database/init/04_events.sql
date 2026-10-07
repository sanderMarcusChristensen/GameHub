USE lol_esports;

-- Stores the latest aggregate statistics for every player.
CREATE TABLE player_statistics (
    player_id BIGINT UNSIGNED NOT NULL,
    games_played INT UNSIGNED NOT NULL DEFAULT 0,
    wins INT UNSIGNED NOT NULL DEFAULT 0,
    losses INT UNSIGNED NOT NULL DEFAULT 0,
    win_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id),
    CONSTRAINT fk_player_statistics_player
        FOREIGN KEY (player_id) REFERENCES players(id)
) ENGINE = InnoDB;

DELIMITER $$

CREATE EVENT ev_refresh_player_statistics
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP + INTERVAL 1 DAY
DO
BEGIN
    INSERT INTO player_statistics (
        player_id,
        games_played,
        wins,
        losses,
        win_rate
    )
    SELECT
        p.id, -- The player's ID
        COUNT(m.id), -- Count completed games
        COALESCE(SUM(m.id IS NOT NULL
            AND g.winner_team_id = pgs.team_id), 0), -- Count games won
        COALESCE(SUM(m.id IS NOT NULL
            AND g.winner_team_id <> pgs.team_id), 0), -- Count games lost
        COALESCE(ROUND(
            100.0 * SUM(m.id IS NOT NULL
                AND g.winner_team_id = pgs.team_id)
            / NULLIF(COUNT(m.id), 0),
            2
        ), 0.00) -- Calculate win rate as a percentage
    FROM players AS p
    LEFT JOIN player_game_stats AS pgs
        ON pgs.player_id = p.id
    LEFT JOIN games AS g
        ON g.id = pgs.game_id
    LEFT JOIN matches AS m
        ON m.id = g.match_id
       AND m.status = 'COMPLETED' -- Only include completed matches
    GROUP BY p.id
    ON DUPLICATE KEY UPDATE
        games_played = VALUES(games_played),
        wins = VALUES(wins),
        losses = VALUES(losses),
        win_rate = VALUES(win_rate);
END$$

DELIMITER ;