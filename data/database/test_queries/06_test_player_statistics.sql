USE gameHub;

SET @test_game = (
    SELECT id
    FROM games
    WHERE match_id = @test_match
      AND game_number = 1
);


-- Insert the player's initial statistics.
CALL sp_save_player_game_stats(
    @test_game,
    @test_player,
    @test_blue,
    @test_champion,
    8,
    2,
    6
);

SELECT
    'Player statistics are saved correctly' AS test_name,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM player_game_stats
            WHERE game_id = @test_game
              AND player_id = @test_player
              AND team_id = @test_blue
              AND champion_id = @test_champion
              AND kills = 8
              AND deaths = 2
              AND assists = 6
        )
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result;


-- Correct the same player's statistics for the same game.
CALL sp_save_player_game_stats(
    @test_game,
    @test_player,
    @test_blue,
    @test_champion,
    10,
    3,
    9
);

SELECT
    'Statistics are updated without duplicate rows' AS test_name,
    COUNT(*) AS actual_rows,
    MAX(kills) AS kills,
    MAX(deaths) AS deaths,
    MAX(assists) AS assists,
    CASE
        WHEN COUNT(*) = 1
         AND MAX(kills) = 10
         AND MAX(deaths) = 3
         AND MAX(assists) = 9
         AND MAX(team_id) = @test_blue
         AND MAX(champion_id) = @test_champion
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result
FROM player_game_stats
WHERE game_id = @test_game
  AND player_id = @test_player;