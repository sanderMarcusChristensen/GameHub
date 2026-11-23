-- ============================================================
-- GameHub MySQL users and privileges
-- Run once as MySQL root or another user-management account.
-- Replace the placeholder passwords before running this script.
-- ============================================================

USE gameHub;

CREATE USER IF NOT EXISTS 'gamehub_app'@'%' 
    IDENTIFIED BY '{{APP_PASSWORD}}';
ALTER USER 'gamehub_app'@'%' 
    IDENTIFIED BY '{{APP_PASSWORD}}';

CREATE USER IF NOT EXISTS 'gamehub_admin'@'%' 
    IDENTIFIED BY '{{ADMIN_PASSWORD}}';
ALTER USER 'gamehub_admin'@'%' 
    IDENTIFIED BY '{{ADMIN_PASSWORD}}';

-- Remove any previously granted privileges before applying a clean policy.
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'gamehub_app'@'%';
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'gamehub_admin'@'%';

-- Application account: normal CRUD and stored-object execution only.
GRANT SELECT, INSERT, UPDATE, DELETE ON gameHub.* TO 'gamehub_app'@'%';
GRANT EXECUTE ON gameHub.* TO 'gamehub_app'@'%';

-- Administrator account: database-management privileges for this schema only.
GRANT ALL PRIVILEGES ON gameHub.* TO 'gamehub_admin'@'%';

FLUSH PRIVILEGES;

SHOW GRANTS FOR 'gamehub_app'@'%';
SHOW GRANTS FOR 'gamehub_admin'@'%';
