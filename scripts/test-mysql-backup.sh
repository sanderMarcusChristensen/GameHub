#!/usr/bin/env bash
# Run from any directory while the GameHub MySQL service is running.
set -euo pipefail
cd "$(dirname "$0")/.."
umask 077
directory=$(mktemp -d "${TMPDIR:-/tmp}/gamehub-backup.XXXXXXXX")
backup="$directory/gameHub.sql"
source_mysql() {
  docker compose exec -T gamehub_db sh -c 'export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"; exec "$@"' sh "$@"
}

# Pause application writes until the source snapshot and dump have both finished.
./mvnw -q -DskipTests compile dependency:build-classpath "-Dmdep.outputFile=$directory/classpath"
manifest() {
  java -cp "target/classes:$(cat "$directory/classpath")" com.gamehub.migrationlogic.graphdb.BackupSnapshotManifest "$1"
}
source_port=$(docker compose port gamehub_db 3306)
MYSQL_PWD=$(docker compose exec -T gamehub_db printenv MYSQL_ROOT_PASSWORD) manifest "${source_port##*:}" > "$directory/source.tsv"
source_mysql mysqldump --single-transaction --quick --routines --events --triggers --set-gtid-purged=OFF --databases gameHub > "$backup.partial"
mv "$backup.partial" "$backup"
echo "Backup: $backup"

# A separate server preserves schema-qualified references and isolates scheduled events.
test_password=$(openssl rand -hex 24)
export MYSQL_ROOT_PASSWORD="$test_password"
target=$(docker run -d -p 127.0.0.1::3306 --tmpfs /var/lib/mysql -e MYSQL_ROOT_PASSWORD -e MYSQL_ROOT_HOST=% mysql:9.5.0 --event-scheduler=OFF)
trap 'docker rm -f "$target" >/dev/null' EXIT
for ((i=0; i<120; i++)); do
  if docker exec -e MYSQL_PWD="$test_password" "$target" mysql --protocol=tcp -h127.0.0.1 -uroot -e 'SELECT 1' >/dev/null 2>&1; then break; fi
  sleep 1
done
docker exec -i -e MYSQL_PWD="$test_password" "$target" mysql -uroot < "$backup"
target_port=$(docker port "$target" 3306/tcp)
MYSQL_PWD="$test_password" manifest "${target_port##*:}" > "$directory/restored.tsv"
diff -u "$directory/source.tsv" "$directory/restored.tsv"
awk -F '\t' '$2 == "rows" {print $1, $3}' "$directory/restored.tsv"
echo "Snapshot values match for every selected row. Evidence: $directory"
