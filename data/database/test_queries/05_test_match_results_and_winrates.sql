USE gameHub;

-- Before any results, both win rates must be NULL.
SELECT
    'Win rates before any results' AS test_name,
    CASE
        WHEN fn_team_match_win_rate(@test_blue) IS NULL
         AND fn_team_game_win_rate(@test_blue) IS NULL
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result;


-- Game 1: Blue wins.
CALL sp_record_game_result(
    @test_match,
    1,
    @test_blue,
    '2026-10-01 12:00:00',
    '2026-10-01 12:35:00'
);

-- One win must not complete a BO3.
SELECT
    'BO3 remains in progress after one game' AS test_name,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM matches
            WHERE id = @test_match
              AND status = 'IN_PROGRESS'
              AND winner_team_id IS NULL
        )
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result;


-- Game 2: Red wins.
CALL sp_record_game_result(
    @test_match,
    2,
    @test_red,
    '2026-10-01 12:50:00',
    '2026-10-01 13:25:00'
);

-- Game 3: Blue wins the series.
CALL sp_record_game_result(
    @test_match,
    3,
    @test_blue,
    '2026-10-01 13:40:00',
    '2026-10-01 14:15:00'
);

SELECT
    'BO3 completes with Blue as winner' AS test_name,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM matches
            WHERE id = @test_match
              AND status = 'COMPLETED'
              AND winner_team_id = @test_blue
        )
        AND (
            SELECT COUNT(*)
            FROM games
            WHERE match_id = @test_match
        ) = 3
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result;


SELECT
    'Blue win rates: 100.00 match / 66.67 game' AS test_name,
    fn_team_match_win_rate(@test_blue) AS match_win_rate,
    fn_team_game_win_rate(@test_blue) AS game_win_rate,
    CASE
        WHEN fn_team_match_win_rate(@test_blue) = 100.00
         AND fn_team_game_win_rate(@test_blue) = 66.67
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result;


SELECT
    'Red win rates: 0.00 match / 33.33 game' AS test_name,
    fn_team_match_win_rate(@test_red) AS match_win_rate,
    fn_team_game_win_rate(@test_red) AS game_win_rate,
    CASE
        WHEN fn_team_match_win_rate(@test_red) = 0.00
         AND fn_team_game_win_rate(@test_red) = 33.33
        THEN 'PASS'
        ELSE 'FAIL'
    END AS test_result;