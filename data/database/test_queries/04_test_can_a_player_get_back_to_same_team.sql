-- First membership period.
CALL sp_save_team_membership(
    @test_blue,
    @test_player,
    '2026-01-01 00:00:00',
    NULL
);

-- Close the first period.
CALL sp_save_team_membership(
    @test_blue,
    @test_player,
    '2026-01-01 00:00:00',
    '2026-03-01 00:00:00'
);

-- Return to the same team.
CALL sp_save_team_membership(
    @test_blue,
    @test_player,
    '2026-08-01 00:00:00',
    NULL
);

SELECT joined_at, left_at
FROM team_players
WHERE team_id = @test_blue
  AND player_id = @test_player
ORDER BY joined_at;