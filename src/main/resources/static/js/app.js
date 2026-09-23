const form = document.querySelector("#player-form");
const playerNameInput = document.querySelector("#player-name");
const statusMessage = document.querySelector("#status");
const result = document.querySelector("#result");
const submitButton = form.querySelector("button");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const playerName = playerNameInput.value.trim();
    if (!playerName) {
        return;
    }

    setLoading(true);

    try {
        const response = await fetch(
            `/api/cito/lol/players/${encodeURIComponent(playerName)}/stats`
        );
        const body = await response.text();

        if (!response.ok) {
            throw new Error(body || `Request failed with status ${response.status}`);
        }

        result.textContent = formatResponse(body);
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

function formatResponse(body) {
    try {
        return JSON.stringify(JSON.parse(body), null, 2);
    } catch {
        return body;
    }
}

function setLoading(isLoading) {
    submitButton.disabled = isLoading;
    submitButton.textContent = isLoading ? "Loading…" : "Get stats";

    if (isLoading) {
        result.hidden = true;
        statusMessage.textContent = "Loading player statistics…";
        statusMessage.classList.remove("error");
    }
}
