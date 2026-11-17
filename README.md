# GameHub

GameHub is an information hub for League of Legends data. The initial version will focus on professional players, their accounts, match history and player statistics.

The project uses the official Riot Games API to retrieve League of Legends account and match data.

## Riot Games development account

To use the Riot Games API, each developer must create a free account on the Riot Developer Portal:

https://developer.riotgames.com/

1. Sign in with a Riot Games account.
2. Open the Riot Developer Portal.
3. Generate a development API key.
4. Copy the generated key into the local `.env` file.

A Riot development API key is temporary and expires after 24 hours. When the key expires, a new key must be generated on the Riot Developer Portal and inserted into the `.env` file.

The API key must never be committed to GitHub or shared publicly.

## CitoAPI account

To use the CitoAPI, each developer must create an account and obtain an API key:

https://citoapi.com/

1. Create or sign in to a CitoAPI account.
2. Open the CitoAPI dashboard.
3. Generate an API key.
4. Copy the API key into the local `.env` file.

The API key must never be committed to GitHub or shared publicly.

## Local setup

Open the `GameHub` directory as your editor workspace. It contains `pom.xml`.
Install JDK 21 and use the included Maven wrapper; a separate Maven install is not required.

Copy `.env.example` to `.env` in this directory and fill in your API keys and local MySQL password:

```sh
cp .env.example .env
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.
Spring Boot loads the root `.env` as a properties file. Use plain `KEY=value` lines
without quotes or `export`. Environment variables can also supply the keys.
The current Cito endpoint requires `CITO_API_KEY`; the Riot key is for future integration.
Set `DB_PASSWORD` to the password for the local MySQL account in `DB_USERNAME`.
The datasource defaults to `gameHub` on `127.0.0.1:3306`; Hibernate validates the
existing tables and does not create or modify them. `.env` is ignored by Git.

Run tests with:

```sh
./mvnw test
```

The context test uses a dummy Cito key and does not call the external API.

## Using account authentication

With GameHub running, use PowerShell (change the port if needed). Registration
requires a unique email and a password of at least 8 characters. It creates a
`USER` account.

```powershell
$baseUrl = "http://localhost:8080"
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$postApi = {
    param($path, $body)
    $csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -WebSession $session
    Invoke-RestMethod "$baseUrl$path" -Method Post -WebSession $session `
        -Headers @{ "X-CSRF-TOKEN" = $csrf.token } `
        -ContentType "application/json" -Body $body
}
$credentials = @{ email = "you@example.com"; password = "choose-a-password" } | ConvertTo-Json
& $postApi "/api/auth/register" $credentials
& $postApi "/api/auth/login" $credentials
Invoke-RestMethod "$baseUrl/api/auth/me" -WebSession $session
& $postApi "/api/auth/logout" "{}"
```

Registration only creates `USER` accounts. Admin roles must be assigned separately.

## Project layout

```text
GameHub/
├── pom.xml
├── mvnw / mvnw.cmd
├── .mvn/wrapper/
├── .env.example
└── src/
    ├── main/
    │   ├── java/com/gamehub/
    │   │   ├── GameHubApplication.java
    │   │   ├── controller/  # Incoming HTTP endpoints
    │   │   ├── client/      # Outgoing API calls (Cito, future Riot integration)
    │   │   └── dao/         # Database access objects
    │   └── resources/
    │       └── application.properties
    └── test/java/com/gamehub/
        └── GameHubApplicationTests.java
```

`dao` stands for Data Access Object. The package is ready for database classes;
no database driver or persistence configuration has been selected yet.
Add a `service` package when business logic needs to coordinate clients and DAOs.

Maven separates application code (`src/main/java`) from tests (`src/test/java`).
The `com/gamehub` folders match the Java package `com.gamehub`, so keep them.
As tests are added, mirror the package of the class under test, such as
`src/test/java/com/gamehub/client`. Editors can display these packages compactly.

The existing endpoint is:
`GET http://localhost:8080/api/cito/lol/players/faker/stats`.

## Frontend

The frontend is in `src/main/resources/static`:

- `index.html` contains the page structure.
- `css/style.css` contains the design.
- `js/app.js` calls the Spring Boot API.

After starting Spring Boot, open `http://localhost:8080/` in a browser.
