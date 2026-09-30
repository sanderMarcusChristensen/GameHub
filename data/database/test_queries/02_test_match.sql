SELECT
    id,
    team_a_id,
    team_b_id,
    status,
    winner_team_id
FROM matches
WHERE team_a_id = team_b_id
   OR (
       winner_team_id IS NOT NULL
       AND winner_team_id NOT IN (team_a_id, team_b_id)
   )
   OR (
       status = 'COMPLETED'
       AND winner_team_id IS NULL
   )
   OR (
       status <> 'COMPLETED'
       AND winner_team_id IS NOT NULL
   );