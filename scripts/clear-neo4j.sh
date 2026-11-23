#!/bin/sh
set -eu

cd "$(dirname "$0")/.."
run_clear() {
    ./mvnw -q spring-boot:run -Dspring-boot.run.profiles=clear-neo4j "$@"
}

run_clear
printf 'Type CLEAR to delete the listed GameHub data: '
IFS= read -r answer || exit 0
if [ "$answer" = CLEAR ]; then
    run_clear -Dspring-boot.run.arguments=--confirm-clear
else
    echo 'Cancelled.'
fi
