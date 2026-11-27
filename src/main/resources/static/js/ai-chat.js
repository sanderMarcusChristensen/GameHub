const aiForm = document.querySelector("#ai-chat-form");
const aiStatus = document.querySelector("#ai-status");
const aiMessages = document.querySelector("#ai-messages");
const aiHistoryList = document.querySelector("#ai-history-list");
const aiNewChat = document.querySelector("#ai-new-chat");
const historyKey = "gamehub-ai-chats-v1";
let chats = [];
let activeChat = null;

try {
    const saved = JSON.parse(localStorage.getItem(historyKey) || "[]");
    if (Array.isArray(saved)) chats = saved;
} catch (error) {
    aiStatus.textContent = "Chat history could not be loaded from this browser.";
}

function saveChats() {
    try {
        localStorage.setItem(historyKey, JSON.stringify(chats));
        return true;
    } catch (error) {
        aiStatus.textContent = "Chat history could not be saved in this browser.";
        return false;
    }
}

function renderHistory() {
    aiHistoryList.replaceChildren();
    for (const chat of chats) {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "ai-history-item";
        button.textContent = chat.title;
        if (chat === activeChat) button.setAttribute("aria-current", "true");
        button.addEventListener("click", () => {
            activeChat = chat;
            aiStatus.textContent = "";
            renderChat();
        });
        aiHistoryList.append(button);
    }
}

function renderChat() {
    aiMessages.replaceChildren();
    if (activeChat) {
        for (const message of activeChat.messages) appendMessage(message.sender, message.content);
    }
    renderHistory();
}

aiNewChat.addEventListener("click", () => {
    activeChat = null;
    aiStatus.textContent = "";
    document.querySelector("#ai-question").value = "";
    renderChat();
});

renderHistory();

function appendMessage(sender, content) {
    const message = document.createElement("article");
    message.className = `ai-message ai-message--${sender}`;
    const label = document.createElement("span");
    label.className = "ai-message-label";
    label.textContent = sender === "user" ? "You" : "AI coach";
    const body = document.createElement("div");
    body.className = "ai-message-body";
    if (sender === "user") {
        body.textContent = content;
    } else {
        renderAnswer(body, content);
    }
    message.append(label, body);
    aiMessages.append(message);
    aiMessages.scrollTop = aiMessages.scrollHeight;
}

function renderAnswer(container, text) {
    let list = null;
    for (const line of text.split(/\r?\n/)) {
        const trimmed = line.trim();
        if (!trimmed) {
            list = null;
            continue;
        }
        const bullet = trimmed.match(/^[-*]\s+(.+)$/);
        if (bullet) {
            if (!list) {
                list = document.createElement("ul");
                container.append(list);
            }
            const item = document.createElement("li");
            appendBoldText(item, bullet[1]);
            list.append(item);
            continue;
        }
        list = null;
        const heading = trimmed.match(/^#{1,6}\s+(.+)$/);
        const element = document.createElement(heading ? "h3" : "p");
        appendBoldText(element, heading ? heading[1] : trimmed);
        container.append(element);
    }
}

function appendBoldText(element, text) {
    const parts = text.split(/\*\*(.+?)\*\*/g);
    parts.forEach((part, index) => {
        if (index % 2 === 0) {
            element.append(document.createTextNode(part));
        } else {
            const strong = document.createElement("strong");
            strong.textContent = part;
            element.append(strong);
        }
    });
}

aiForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const questionInput = document.querySelector("#ai-question");
    const question = questionInput.value.trim();
    if (!question) return;

    const button = aiForm.querySelector("button");
    if (!activeChat) {
        activeChat = { title: question.slice(0, 60), messages: [] };
        chats.unshift(activeChat);
    }
    const requestChat = activeChat;
    requestChat.messages.push({ sender: "user", content: question });
    const saved = saveChats();
    renderChat();
    button.disabled = true;
    if (saved) aiStatus.textContent = "AI coach is thinking…";
    try {
        const csrfResponse = await fetch("/api/auth/csrf");
        if (!csrfResponse.ok) throw new Error("Could not start a session.");
        const csrf = await csrfResponse.json();
        const response = await fetch("/ai/chat", {
            method: "POST",
            headers: { "Content-Type": "application/json", [csrf.headerName]: csrf.token },
            body: JSON.stringify({ question })
        });
        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.detail || `Request failed (${response.status}). Check that AI is enabled and Ollama is running.`);
        }
        const result = await response.json();
        requestChat.messages.push({ sender: "coach", content: result.analysis });
        const saved = saveChats();
        if (requestChat === activeChat) {
            renderChat();
            if (questionInput.value.trim() === question) questionInput.value = "";
            if (saved) aiStatus.textContent = "";
        }
    } catch (error) {
        if (requestChat === activeChat) {
            aiStatus.textContent = `Could not generate an answer. ${error.message}`;
        }
    } finally {
        button.disabled = false;
    }
});
