const form = document.querySelector("#player-form");
const playerNameInput = document.querySelector("#player-name");
const statusMessage = document.querySelector("#status");
const result = document.querySelector("#result");
const submitButton = form.querySelector("button");

const example = document.querySelector("#example");
example.addEventListener("click", () => {
    playerNameInput.value = "faker";
    form.requestSubmit();
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const playerName = playerNameInput.value.trim();
    if (!playerName) {
        return;
    }

    form.closest("main").classList.add("lookup-active");
    setLoading(true);

    try {
        const response = await fetch(
            `/api/cito/lol/players/${encodeURIComponent(playerName)}/stats`
        );
        const body = await response.text();

        if (!response.ok) {
            throw new Error(body || `Request failed with status ${response.status}`);
        }

        renderPlayer(playerName, JSON.parse(body));
        result.hidden = false;
        statusMessage.textContent = `Stats loaded for ${playerName}.`;
        statusMessage.classList.remove("error");
    } catch (error) {
        result.hidden = true;
        statusMessage.textContent = `Could not load player stats: ${error.message}`;
        statusMessage.classList.add("error");
    } finally {
        setLoading(false);
    }
});

function renderPlayer(name, stats) {
    if (!Number.isFinite(stats?.gamesPlayed)) throw new Error("Missing player record.");
    const number = (value, digits = 0) => Number.isFinite(value)
        ? value.toLocaleString("en-US", { maximumFractionDigits: digits }) : "—";
    const percent = value => Number.isFinite(value) ? number(value * 100, 1) + "%" : "—";
    const show = (id, value) => document.getElementById(id).textContent = value;
    show("handle", name);
    show("games", number(stats.gamesPlayed));
    show("rate", percent(stats.winRate));
    show("record", number(stats.wins) + " wins · " + number(stats.losses) + " losses");
    show("kda", number(stats.avgKda, 2));
    show("kills", number(stats.avgKills, 2));
    show("deaths", number(stats.avgDeaths, 2));
    show("assists", number(stats.avgAssists, 2));
    document.querySelector("#bar").style.width = Math.min(100, Math.max(0, (stats.winRate ?? 0) * 100)) + "%";
    const coverage = Number.isFinite(stats.coverage?.detailedStatsGames)
        ? "Based on " + number(stats.coverage.detailedStatsGames) + " detailed games." : "";
    const tiles = [
        ["Farm", "m4 16 12-12 4 4L8 20z M6 12l6 6", number(stats.avgCsPerMin, 2) + " CS/min",
            number(stats.avgCs, 1) + " CS per game. " + coverage.replace("Based on", "CS/min based on")],
        ["Combat", "m13 2-9 12h7l-1 8 10-14h-8z", number(stats.avgDamagePerMin) + " damage/min",
            percent(stats.killParticipation) + " kill participation. " + coverage],
        ["Vision", "M2 12Q12-2 22 12Q12 26 2 12Z M15 12a3 3 0 1 1-6 0 3 3 0 1 1 6 0", number(stats.avgVisionScore, 1) + " vision score",
            "Average vision score per detailed game. " + coverage]
    ];
    const metrics = document.getElementById("metrics");
    metrics.replaceChildren();
    for (const [label, path, value, description] of tiles) {
        const tile = document.createElement("details");
        tile.innerHTML = `<summary><svg viewBox="0 0 24 24" aria-hidden="true"><path d="${path}"/></svg> ${label}<b>${value}</b></summary><p></p>`;
        tile.querySelector("p").textContent = description;
        metrics.append(tile);
    }
}

function setLoading(isLoading) {
    submitButton.disabled = isLoading;
    example.disabled = isLoading;
    submitButton.textContent = isLoading ? "Loading…" : "Get stats →";

    if (isLoading) {
        result.hidden = true;
        statusMessage.textContent = "Loading player statistics…";
        statusMessage.classList.remove("error");
    }
}
