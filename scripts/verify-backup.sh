#!/usr/bin/env bash
# Restore a GameHub SQL backup into a temporary MySQL container.
set -euo pipefail

# Prevent Git Bash from converting Unix paths used by Docker.
export MSYS_NO_PATHCONV=1


# Use the supplied backup file, or automatically select the newest one.
if [[ $# -ge 1 ]]; then
  BACKUP_FILE="$1"
else
  BACKUP_FILE=$(ls -t backups/gameHub_*.sql 2>/dev/null | head -n 1)

  if [[ -z "$BACKUP_FILE" ]]; then
    echo "Error: no backup files found in backups/." >&2
    exit 1
  fi
fi

echo "Using backup: $BACKUP_FILE"

if [[ ! -f "$BACKUP_FILE" ]]; then
  echo "Error: backup file not found: $BACKUP_FILE" >&2
  exit 1
fi

if [[ ! -s "$BACKUP_FILE" ]]; then
  echo "Error: backup file is empty: $BACKUP_FILE" >&2
  exit 1
fi

CONTAINER_NAME="gamehub-backup-test-$$"
MYSQL_ROOT_PASSWORD=$(openssl rand -hex 16)

cleanup() {
  docker rm -f "$CONTAINER_NAME" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "Starting temporary MySQL container..."
docker run -d \
  --name "$CONTAINER_NAME" \
  --tmpfs /var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD="$MYSQL_ROOT_PASSWORD" \
  -e MYSQL_ROOT_HOST="%" \
  mysql:9.5.0 \
  --event-scheduler=OFF >/dev/null

echo "Waiting for MySQL to become ready..."
READY=false
for ((attempt = 1; attempt <= 60; attempt++)); do
  if docker exec \
    -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" \
    "$CONTAINER_NAME" \
    mysqladmin --user=root ping --silent >/dev/null 2>&1; then
    READY=true
    break
  fi
  sleep 2
done

if [[ "$READY" != true ]]; then
  echo "Error: temporary MySQL server did not become ready." >&2
  docker logs "$CONTAINER_NAME" >&2
  exit 1
fi

echo "Restoring backup..."
docker exec -i \
  -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" \
  "$CONTAINER_NAME" \
  mysql --user=root < "$BACKUP_FILE"

echo
echo "Backup restored successfully. Tables in gameHub:"
docker exec \
  -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" \
  "$CONTAINER_NAME" \
  mysql --user=root --database=gameHub --execute="SHOW TABLES;"

echo
echo "Row counts in selected tables:"
docker exec \
  -e MYSQL_PWD="$MYSQL_ROOT_PASSWORD" \
  "$CONTAINER_NAME" \
  mysql --user=root --database=gameHub --table --execute="
    SELECT 'players' AS table_name, COUNT(*) AS row_count FROM players
    UNION ALL
    SELECT 'teams', COUNT(*) FROM teams
    UNION ALL
    SELECT 'matches', COUNT(*) FROM matches
    UNION ALL
    SELECT 'games', COUNT(*) FROM games;
  "

echo
echo "Verification successful: the backup was restored and the selected tables could be queried."
