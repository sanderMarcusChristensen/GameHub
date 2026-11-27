-- ============================================================
-- GameHub MySQL users and privileges
-- Runs on first startup in a fresh Docker volume.
-- The MySQL entrypoint does not expand environment variables in .sql
-- files, so the password below is a literal value. 123456 is deliberately
-- used for all four accounts to make it obvious these are throwaway local
-- demo credentials, not placeholders. The same value is in .env.example.
-- Not for production use.
-- ============================================================

USE gameHub;

-- 1. Backend application: least privilege (CRUD and stored routines).
CREATE USER IF NOT EXISTS 'gamehub_app'@'%'
    IDENTIFIED BY '123456';
ALTER USER 'gamehub_app'@'%'
    IDENTIFIED BY '123456';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'gamehub_app'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON gameHub.* TO 'gamehub_app'@'%';
GRANT EXECUTE ON gameHub.* TO 'gamehub_app'@'%';

-- 2. Administrator: full control of this schema only.
CREATE USER IF NOT EXISTS 'gamehub_admin'@'%'
    IDENTIFIED BY '123456';
ALTER USER 'gamehub_admin'@'%'
    IDENTIFIED BY '123456';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'gamehub_admin'@'%';
GRANT ALL PRIVILEGES ON gameHub.* TO 'gamehub_admin'@'%';

-- 3. Read-only: SELECT on every table and view, including app_users.
CREATE USER IF NOT EXISTS 'gamehub_readonly'@'%'
    IDENTIFIED BY '123456';
ALTER USER 'gamehub_readonly'@'%'
    IDENTIFIED BY '123456';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'gamehub_readonly'@'%';
GRANT SELECT ON gameHub.* TO 'gamehub_readonly'@'%';

-- 4. Restricted reader: all domain data, but no access to the sensitive
--    email and password_hash columns of app_users. Table-level grants are
--    required here because MySQL grants are additive: a wildcard grant
--    cannot be negated with a later REVOKE on one table.
CREATE USER IF NOT EXISTS 'gamehub_restricted'@'%'
    IDENTIFIED BY '123456';
ALTER USER 'gamehub_restricted'@'%'
    IDENTIFIED BY '123456';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.players TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.teams TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.team_players TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.champions TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.champion_roles TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.champions_has_champion_roles TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.tournaments TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.tournament_teams TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.matches TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.games TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.player_game_stats TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.match_status_history TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.player_statistics TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.vw_player_leaderboard TO 'gamehub_restricted'@'%';
GRANT SELECT ON gameHub.vw_match_overview TO 'gamehub_restricted'@'%';
GRANT SELECT (id, role, is_enabled, created_at) ON gameHub.app_users
    TO 'gamehub_restricted'@'%';

FLUSH PRIVILEGES;

SHOW GRANTS FOR 'gamehub_app'@'%';
SHOW GRANTS FOR 'gamehub_admin'@'%';
SHOW GRANTS FOR 'gamehub_readonly'@'%';
SHOW GRANTS FOR 'gamehub_restricted'@'%';
