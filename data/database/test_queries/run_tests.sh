#!/usr/bin/env bash
set -euo pipefail

cd -- "$(dirname -- "${BASH_SOURCE[0]}")"

shopt -s nullglob
files=(./*.sql)

if [ "${#files[@]}" -eq 0 ]; then
    echo "[FEJL] Ingen SQL-testfiler fundet."
    exit 1
fi

if ! docker info >/dev/null 2>&1; then
    echo "[FEJL] Kan ikke kontakte Docker. Er Docker startet?"
    exit 1
fi

if [ "$(docker inspect -f '{{.State.Running}}' gamehub_db 2>/dev/null || true)" != "true" ]; then
    echo "[FEJL] Containeren gamehub_db kører ikke."
    echo "Start databasen med: docker compose up -d"
    exit 1
fi

work_dir=$(mktemp -d)
trap 'rm -rf -- "$work_dir"' EXIT

combined="$work_dir/tests.sql"
mapping="$work_dir/lines.tsv"
output="$work_dir/output.log"

: > "$combined"
: > "$mapping"

line_number=1
test_number=0

# Combine files so session variables survive between tests.
for file in "${files[@]}"; do
    test_number=$((test_number + 1))
    name=$(basename -- "$file")

    # Names are printed by Bash; SQL markers contain numbers only.
    printf '%s\t%s\t%s\n' \
        "$test_number" "$line_number" "$name" >> "$mapping"

    printf "SELECT '========== TEST %s ==========' AS test_file;\n" \
        "$test_number" >> "$combined"

    line_number=$((line_number + 1))

    while IFS= read -r line || [ -n "$line" ]; do
        printf '%s\n' "$line" >> "$combined"
        line_number=$((line_number + 1))
    done < "$file"

    printf '\n' >> "$combined"
    line_number=$((line_number + 1))
done

echo
echo "========================================"
echo " SQL-tests · gameHub"
echo "========================================"

while IFS=$'\t' read -r number start name; do
    printf '  Test %s: %s\n' "$number" "$name"
done < "$mapping"

echo
echo "Kører tests i én MySQL-forbindelse..."
echo

# --table makes query results easier to read.
# --verbose prints the SQL being executed.
if docker exec -i -e MYSQL_PWD=123456 \
    gamehub_db mysql \
    --user=root \
    --database=gameHub \
    --table \
    --verbose \
    < "$combined" > "$output" 2>&1
then
    cat "$output"

    echo "[OK] Alle testfiler er kørt uden SQL-fejl."
    echo ""
    echo "Se kolonnen 'test_result' i resultaterne ovenfor:"
    echo "  PASS = Testen gav det forventede resultat."
    echo "  FAIL = Testen gav et forkert resultat og skal undersøges."
    echo ""
    echo "Alle tests er først bestået, når alle kontrolrækker viser PASS."
else
    cat "$output"

    error_line=$(
        sed -nE \
            's/^ERROR [0-9]+ .* at line ([0-9]+):.*/\1/p' \
            "$output" |
        head -n 1
    )

    echo
    echo "========================================"
    echo "[FEJL] Testkørslen stoppede."

    if [ -n "$error_line" ]; then
        failed_file=""
        failed_start=0

        while IFS=$'\t' read -r number start name; do
            if [ "$start" -le "$error_line" ]; then
                failed_file="$name"
                failed_start="$start"
            fi
        done < "$mapping"

        if [ -n "$failed_file" ]; then
            # Subtract the generated marker before the file.
            original_line=$((error_line - failed_start))

            echo "Fil: $failed_file"

            if [ "$original_line" -gt 0 ]; then
                echo "SQL starter omkring linje: $original_line"
            fi
        fi
    else
        echo "Kunne ikke finde et SQL-linjenummer."
        echo "Kontrollér Docker- eller forbindelsesfejlen ovenfor."
    fi

    echo "Efterfølgende tests blev ikke kørt."
    echo "========================================"
    exit 1
fi