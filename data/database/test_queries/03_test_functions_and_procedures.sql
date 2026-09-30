USE lol_esports_v2;

INSERT INTO teams (name, short_name)
VALUES ('TEST Blue Team', 'TBLUE');

SET @test_blue = LAST_INSERT_ID();


INSERT INTO teams (name, short_name)
VALUES ('TEST Red Team', 'TRED');

SET @test_red = LAST_INSERT_ID();


-- This team does not participate in the match.
INSERT INTO teams (name, short_name)
VALUES ('TEST Outside Team', 'TOUT');

SET @test_outside = LAST_INSERT_ID();


INSERT INTO players (nickname)
VALUES ('TEST Player');

SET @test_player = LAST_INSERT_ID();


INSERT INTO champions (name, released_at)
VALUES ('TEST Champion', '2020-01-01');

SET @test_champion = LAST_INSERT_ID();


INSERT INTO tournaments (name, start_date, end_date)
VALUES (
    'TEST Tournament',
    '2026-10-01 09:00:00',
    '2026-10-03 20:00:00'
);

SET @test_tournament = LAST_INSERT_ID();


INSERT INTO tournament_teams (tournament_id, team_id)
VALUES
    (@test_tournament, @test_blue),
    (@test_tournament, @test_red);


INSERT INTO matches (
    tournament_id,
    team_a_id,
    team_b_id,
    format
)
VALUES (
    @test_tournament,
    @test_blue,
    @test_red,
    'BO3'
);

SET @test_match = LAST_INSERT_ID();