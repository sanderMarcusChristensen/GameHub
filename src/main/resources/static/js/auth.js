const authForm = document.querySelector("#auth-form");
const authEmail = document.querySelector("#auth-email");
const authPassword = document.querySelector("#auth-password");
const authStatus = document.querySelector("#auth-status");
const signedOut = document.querySelector("#auth-signed-out");
const signedIn = document.querySelector("#auth-signed-in");
const registerButton = document.querySelector("#register-button");
const logoutButton = document.querySelector("#logout-button");

let registering = false;

function showUser(user) {
    signedOut.hidden = Boolean(user);
    signedIn.hidden = !user;
    document.querySelector("#auth-user").textContent = user?.email ?? "";
    authPassword.value = "";
}

async function authPost(path, body) {
    const csrfResponse = await fetch("/api/auth/csrf");
    if (!csrfResponse.ok) throw new Error("Could not start a session. Please try again.");
    const csrf = await csrfResponse.json();
    const response = await fetch(path, {
        method: "POST",
        headers: { "Content-Type": "application/json", [csrf.headerName]: csrf.token },
        body: JSON.stringify(body)
    });
    if (!response.ok) {
        if (response.status === 401) throw new Error("Incorrect email or password.");
        if (response.status === 409) throw new Error("This email is already registered.");
        if (response.status === 400) throw new Error("Check your email and password. Passwords must be at least 8 characters when creating an account.");
        throw new Error("Request failed. Please try again.");
    }
    return response.status === 204 ? null : response.json();
}

async function submitAuth(register) {
    if (!authForm.reportValidity()) return;
    const credentials = { email: authEmail.value.trim(), password: authPassword.value };
    authStatus.textContent = register ? "Creating account…" : "Signing in…";
    authStatus.classList.remove("error");
    authForm.querySelectorAll("button").forEach(button => button.disabled = true);
    registerButton.disabled = true;
    let accountCreated = false;
    try {
        if (register) {
            await authPost("/api/auth/register", credentials);
            accountCreated = true;
        }
        const user = await authPost("/api/auth/login", credentials);
        showUser(user);
        authStatus.textContent = register ? "Account created." : "Signed in.";
    } catch (error) {
        authStatus.textContent = accountCreated
            ? `Account created, but sign-in failed. ${error.message}`
            : error.message;
        authStatus.classList.add("error");
    } finally {
        registerButton.disabled = false;
        authForm.querySelectorAll("button").forEach(button => button.disabled = false);
    }
}

authForm.addEventListener("submit", event => {
    event.preventDefault();
    submitAuth(registering);
});
registerButton.addEventListener("click", () => {
    registering = !registering;
    document.querySelector("#auth-heading").textContent = registering ? "Create your account" : "Sign in to GameHub";
    document.querySelector("#auth-kicker").textContent = registering ? "Join GameHub" : "Welcome back";
    document.querySelector("#auth-intro").textContent = registering ? "Start building your esports registry." : "Access your player and team registry.";
    document.querySelector("#auth-submit").textContent = registering ? "Create account →" : "Sign in →";
    document.querySelector("#switch-label").textContent = registering ? "Already have an account?" : "Don’t have an account?";
    registerButton.textContent = registering ? "Sign in" : "Create account";
    authPassword.minLength = registering ? 8 : 1;
    authPassword.placeholder = registering ? "8+ characters" : "";
    authPassword.autocomplete = registering ? "new-password" : "current-password";
    authPassword.value = "";
    authStatus.textContent = "";
    authEmail.focus();
});
logoutButton.addEventListener("click", async () => {
    logoutButton.disabled = true;
    try {
        await authPost("/api/auth/logout", {});
        showUser(null);
        authStatus.textContent = "Signed out.";
        authStatus.classList.remove("error");
    } catch (error) {
        authStatus.textContent = error.message;
        authStatus.classList.add("error");
    } finally {
        logoutButton.disabled = false;
    }
});

fetch("/api/auth/me")
    .then(response => response.ok ? response.json() : null)
    .then(user => { if (user) showUser(user); })
    .catch(() => {
        authStatus.textContent = "Could not check your session.";
        authStatus.classList.add("error");
    });

