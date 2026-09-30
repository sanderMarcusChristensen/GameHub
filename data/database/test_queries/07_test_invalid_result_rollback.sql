USE gameHub;

DELIMITER $$

CREATE PROCEDURE test_assert_invalid_result_rollback()
BEGIN
    DECLARE v_error_received BOOLEAN DEFAULT FALSE;
    DECLARE v_error_message TEXT DEFAULT NULL;

    -- Catch the expected business-rule error from the procedure.
    BEGIN
        DECLARE CONTINUE HANDLER FOR SQLSTATE '45000'
        BEGIN
            GET DIAGNOSTICS CONDITION 1
                v_error_message = MESSAGE_TEXT;

            SET v_error_received = TRUE;
        END;

        CALL sp_record_game_result(
            @test_match,
            2,
            @test_blue,
            '2026-10-01 12:50:00',
            '2026-10-01 13:25:00'
        );
    END;

    SELECT
        'Invalid result change is rejected' AS test_name,
        v_error_message AS received_error,
        CASE
            WHEN v_error_received = TRUE
             AND v_error_message =
                 'These results exceed the series win limit.'
            THEN 'PASS'
            ELSE 'FAIL'
        END AS test_result;


    SELECT
        'Rollback preserves the original game and match' AS test_name,
        CASE
            WHEN EXISTS (
                SELECT 1
                FROM games
                WHERE match_id = @test_match
                  AND game_number = 2
                  AND winner_team_id = @test_red
                  AND started_at = '2026-10-01 12:50:00'
                  AND ended_at = '2026-10-01 13:25:00'
            )
            AND EXISTS (
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
END$$

DELIMITER ;

CALL test_assert_invalid_result_rollback();

DROP PROCEDURE test_assert_invalid_result_rollback;