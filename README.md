# GameHub

GameHub is an information hub for League of Legends data. The initial version will focus on professional players, their accounts, match history and player statistics.

The project uses the official Riot Games API to retrieve League of Legends account and match data.

For local Neo4j imports and test resets, see the [migration and cleanup commands](doc/neo4j/README.md#kommandoer-til-migrering-og-rydning).

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

Open a second PowerShell terminal and run this once. Change `8080` if the app
uses another port.

```powershell
$baseUrl = "http://localhost:8080"
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
```

### Registration

Paste this to create a `USER` account. Choose a unique email and a password with
at least 8 characters.

```powershell
$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -WebSession $session
$body = @{ email = "you@example.com"; password = "choose-a-password" } | ConvertTo-Json

Invoke-RestMethod "$baseUrl/api/auth/register" -Method Post `
    -WebSession $session `
    -Headers @{ "X-CSRF-TOKEN" = $csrf.token } `
    -ContentType "application/json" -Body $body
```

### Login

Paste this to sign in with the registered account.

```powershell
$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -WebSession $session
$body = @{ email = "you@example.com"; password = "choose-a-password" } | ConvertTo-Json

Invoke-RestMethod "$baseUrl/api/auth/login" -Method Post `
    -WebSession $session `
    -Headers @{ "X-CSRF-TOKEN" = $csrf.token } `
    -ContentType "application/json" -Body $body
```

### Check who is logged in

Paste this to call `/api/auth/me`:

```powershell
Invoke-RestMethod "$baseUrl/api/auth/me" -WebSession $session
```

### Logout

Paste this to end the login session:

```powershell
$csrf = Invoke-RestMethod "$baseUrl/api/auth/csrf" -WebSession $session

Invoke-RestMethod "$baseUrl/api/auth/logout" -Method Post `
    -WebSession $session `
    -Headers @{ "X-CSRF-TOKEN" = $csrf.token }
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

## Additional backend functionality

The backend also includes relational database functionality for managing players, teams, tournaments and matches. MySQL is used as the relational database, with Spring Data JPA repositories and services handling database access. The package description in the earlier Project layout section reflects the initial project structure; the current code also uses repository, documentrepository, graphrepository, service, migration and migrationlogic packages for database access and migration work.

The current Docker Compose configuration maps MySQL to host port 3307 (3307:3306). When using the supplied Docker Compose setup, set DB_PORT=3307 in .env; MySQL still listens on port 3306 inside the container.
Player endpoints
The player API is available under /api/players:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/players` | Returns players with pagination, sorting and filtering support. |
| `GET` | `/api/players/{id}` | Returns one player. |
| `POST` | `/api/players` | Creates a player. |
| `PUT` | `/api/players/{id}` | Updates a player. |
| `DELETE` | `/api/players/{id}` | Deletes a player. |


Team endpoints
The team API is available under /api/teams:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/teams` | Returns all teams. |
| `GET` | `/api/teams/{id}` | Returns one team. |
| `POST` | `/api/teams` | Creates a team. |
| `PUT` | `/api/teams/{id}` | Updates a team. |
| `DELETE` | `/api/teams/{id}` | Deletes a team. |


Tournament and match endpoints
Tournament management is available under /api/tournaments:

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/tournaments` | Creates a tournament. |
| `POST` | `/api/tournaments/{id}/teams/{teamId}` | Registers an existing team in a tournament. |
| `POST` | `/api/tournaments/{id}/matches` | Schedules a match between two teams registered in the tournament. |
| `GET` | `/api/tournaments/{id}/standings` | Returns the current tournament standings. |


Game results are submitted through POST /api/matches/{id}/games/results. The request records an individual game's number, winning team and start/end times. The database procedure updates the match status and determines the series winner when enough games have been completed.
MySQL stored procedures, functions, triggers and views
The SQL initialization scripts in data/database/init create additional database objects:
- sp_record_game_result records a game result, validates it against the match format and updates the match result/status. It is called by the match result endpoint.
- sp_get_tournament_standings calculates standings for a tournament. It is called by the tournament standings endpoint.
- sp_save_team_membership manages a player's membership period in a team.
- sp_save_player_game_stats saves player statistics for a game.
- fn_team_match_win_rate calculates a team's match win rate.
- fn_team_game_win_rate calculates a team's game win rate.
- trg_matches_status_history records changes to match status in match_status_history.
- ev_refresh_player_statistics refreshes the aggregated player statistics table daily. MySQL's event scheduler must be enabled for this event to run.
- vw_player_leaderboard and vw_match_overview provide read-only views for player statistics and match information.
The database definitions are in 01_gamehub_v7.sql, 03_triggers.sql, 04_events.sql and 05_views.sql under data/database/init.
MongoDB migration
GameHub includes a one-time migration from the relational MySQL database to MongoDB. The migration reads the current MySQL data, maps relational entities into MongoDB documents and saves them in the gamehub_documents database. It does not continuously synchronize changes between MySQL and MongoDB. Run the migration again when you want to copy the current source records. Records deleted from MySQL are not automatically deleted from MongoDB by this migration, so it should not be treated as a continuously synchronized mirror.

The migration is activated with the Spring profile migrate-mongo. The migration copies the following data into MongoDB collections:

| MongoDB collection | Migrated data |
| --- | --- |
| `players` | Players, team membership history and aggregated player statistics. |
| `teams` | Teams. |
| `champions` | Champions and their roles. |
| `tournaments` | Tournaments and registered teams. |
| `matches` | Matches, games, player game statistics and match status history. |
| `users` | Application users. |

1. Configure MongoDB
Make sure the root .env file contains MONGO_ROOT_USERNAME, MONGO_ROOT_PASSWORD and MONGODB_URI. These values must match. For the default local setup, use admin as the username and replace DIN_ADGANGSKODE in the URI with the same password used for MONGO_ROOT_PASSWORD:

MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=your-local-password
MONGODB_URI=mongodb://admin:your-local-password@localhost:27017/gamehub_documents?authSource=admin

2. Start MySQL and MongoDB
Run these commands from the project directory containing docker-compose.yml:

docker compose up -d gamehub_db mongodb

MySQL must already be initialized and contain the data you want to migrate. On a new installation, the MySQL initialization scripts create and populate the database when the MySQL Docker volume is first created. MongoDB must also be running before starting the migration.

3. Run the migration
Run the command for your operating system from the project directory. Do not start the normal application at the same time. The migration profile activates MigrationService, which runs when Spring Boot starts.

Windows:

.\mvnw.cmd spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"

macOs/Linux:
./mvnw spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"

Wait for the log message Migration finished!. If the process remains running, press Ctrl+C after the migration has finished. Then start the application normally using the command in Local setup.

4. Verify the MongoDB data
The following command checks the number of player documents in MongoDB. It prompts for the MongoDB password. On a fresh installation, this count should match the MySQL player count:

docker compose exec mongodb mongosh gamehub_documents --username admin --authenticationDatabase admin --eval "db.players.countDocuments({})" --password

You can replace players in the query with teams, champions, tournaments, matches or users to check the other collections.