#!/usr/bin/env bash
# Create a timestamped logical backup of the GameHub MySQL database.
set -euo pipefail

# Run from the project root, regardless of the caller's current directory.
cd "$(dirname "$0")/.."

BACKUP_DIR="backups"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="$BACKUP_DIR/gameHub_$TIMESTAMP.sql"
TEMP_FILE="$BACKUP_FILE.partial"

mkdir -p "$BACKUP_DIR"
trap 'rm -f "$TEMP_FILE"' EXIT

echo "Creating MySQL backup..."

docker compose exec -T gamehub_db sh -c '
  export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"
  exec mysqldump \
    --user=root \
    --single-transaction \
    --quick \
    --routines \
    --events \
    --triggers \
    --set-gtid-purged=OFF \
    --databases gameHub
' > "$TEMP_FILE"

if [[ ! -s "$TEMP_FILE" ]]; then
  echo "Error: the backup file is empty." >&2
  exit 1
fi

mv "$TEMP_FILE" "$BACKUP_FILE"
trap - EXIT

echo "Backup completed successfully:"
echo "$BACKUP_FILE"
echo "File size: $(du -h "$BACKUP_FILE" | cut -f1)"
