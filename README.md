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

The backend exposes a passthrough endpoint for this API:
`GET http://localhost:8080/api/cito/lol/players/{playerIdOrSlug}/stats`.

## Local setup

Open the `GameHub` directory as your editor workspace. It contains `pom.xml`.
Install JDK 21 and use the included Maven wrapper; a separate Maven install is not required.

Copy `.env.example` to `.env` in this directory and fill in your API keys and the three MySQL passwords. For the full Docker-based walkthrough, see [PROJECT_SETUP.md](PROJECT_SETUP.md).

```sh
cp .env.example .env
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.
Spring Boot loads the root `.env` as a properties file. Use plain `KEY=value` lines
without quotes or `export`. Environment variables can also supply the keys.
The current Cito endpoint requires `CITO_API_KEY`; the Riot key is for future integration.
The backend connects as `MYSQL_APP_USERNAME` with `MYSQL_APP_PASSWORD`. The
datasource points at `MYSQL_DATABASE` on `MYSQL_HOST:MYSQL_PORT`, which defaults to `gameHub`
on `127.0.0.1:3307` with the supplied Compose setup. Hibernate validates the
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

There are 2 ways to register and login, either use the apps site to insert your registration/login credentials or paste this to create a `USER` account. Choose a unique email and a password with
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

## MySQL users and privileges

[06_users_and_privileges.sql](data/database/init/06_users_and_privileges.sql)
creates four accounts on the first start of a fresh MySQL volume:

| Account | Privileges |
| --- | --- |
| `gamehub_app` | SELECT, INSERT, UPDATE, DELETE and EXECUTE on `gameHub` — the backend account, limited to what the application needs. |
| `gamehub_admin` | ALL PRIVILEGES on `gameHub`. |
| `gamehub_readonly` | SELECT on every table and view. |
| `gamehub_restricted` | SELECT on domain tables and views; only `id`, `role`, `is_enabled` and `created_at` from `app_users`, so `email` and `password_hash` are hidden. |

The passwords are fixed local demo values mirrored in `.env.example`; the MySQL
image does not expand environment variables in `.sql` files, so both files must
be updated together. Only `MYSQL_APP_*` is used by the backend. Set
`MYSQL_ROOT_PASSWORD` to your own value.

The accounts are created only when the volume is empty. To apply changes, remove
the `gamehub_gamehub_db_data` volume (this deletes the local MySQL data) or run
`ALTER USER` inside the container. Use `root` only for maintenance.

Test `gamehub_app`:

```sql
USE `gameHub`;
SELECT * FROM app_users LIMIT 5;
CREATE TABLE privilege_test (id INT);
```

The `SELECT` should succeed and `CREATE TABLE` should be denied. As
`gamehub_restricted`, `SELECT * FROM app_users` should fail on the hidden
columns, while `SELECT id, role FROM app_users` succeeds.

## Project layout

```text
GameHub/
├── docker-compose.yml        # MySQL, MongoDB and Neo4j for local development
├── data/database/            # SQL schema, seed data, triggers, events, views,
│                             # users/privileges and test queries
├── scripts/                  # Backup, restore verification and Neo4j reset scripts
├── doc/                      # Assignment briefs, report, ERDs and diagrams
└── src/
    ├── main/java/com/gamehub/
    │   ├── GameHubApplication.java
    │   ├── controller/       # HTTP endpoints
    │   ├── dto/              # Request/response records exchanged over the API
    │   ├── service/          # Business logic and transaction boundaries
    │   ├── mapper/           # Entity <-> DTO conversion
    │   ├── specification/    # JPA filtering criteria
    │   ├── security/         # Spring Security configuration
    │   ├── exception/        # Custom exceptions and the global handler
    │   ├── enums/            # Enums shared by all three data models
    │   │
    │   ├── entity/           # Domain models, one subpackage per database
    │   │   ├── jpa/          #   MySQL @Entity classes and composite keys
    │   │   ├── document/     #   MongoDB @Document classes
    │   │   └── graph/        #   Neo4j @Node records
    │   │
    │   ├── repository/       # Data access, mirrors entity/ exactly
    │   │   ├── jpa/          #   Spring Data JPA repositories
    │   │   ├── document/     #   Spring Data MongoDB repositories
    │   │   └── graph/        #   Spring Data Neo4j repositories and Cypher queries
    │   │
    │   └── migrationlogic/   # One-time RDBMS -> MongoDB / Neo4j migration
    │       ├── documentdb/   #   Migrator and document mappers
    │       └── graphdb/      #   Migrator and graph mappers
    └── test/java/com/gamehub/  # Unit and integration tests
```

`entity/` and `repository/` use the same three-way split, so each database
solution occupies one folder per layer. The web frontend lives in
`src/main/resources/static` and is described under [Frontend](#frontend).

Migration code is kept in `migrationlogic/` and only runs under the
`migrate-mongo` and `migrate-neo4j` profiles, so it never runs during normal
startup. See [MongoDB migration](#mongodb-migration) and
[doc/neo4j/README.md](doc/neo4j/README.md) for the Neo4j equivalent.

## Frontend

The frontend is in `src/main/resources/static`:

- `index.html` contains the page structure.
- `css/style.css` contains the design.
- `js/app.js` calls the Spring Boot API.

After starting Spring Boot, open `http://localhost:8080/` in a browser.

## Additional backend functionality

The backend includes relational database functionality for managing players, teams, tournaments and matches. MySQL is the relational database, with Spring Data JPA repositories and services handling database access. MongoDB and Neo4j use the `document` and `graph` packages shown in [Project layout](#project-layout); their data is loaded by the migrator.

### Player endpoints

The player API is available under `/api/players`:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/players` | Returns players with pagination, sorting and filtering support. |
| `GET` | `/api/players/{id}` | Returns one player. |
| `POST` | `/api/players` | Creates a player. |
| `PUT` | `/api/players/{id}` | Updates a player. |
| `DELETE` | `/api/players/{id}` | Deletes a player. |

### Team endpoints

The team API is available under `/api/teams`:

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/teams` | Returns all teams. |
| `GET` | `/api/teams/{id}` | Returns one team. |
| `POST` | `/api/teams` | Creates a team. |
| `PUT` | `/api/teams/{id}` | Updates a team. |
| `DELETE` | `/api/teams/{id}` | Deletes a team. |

### Tournament and match endpoints

Tournament management is available under `/api/tournaments`:

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/tournaments` | Creates a tournament. |
| `POST` | `/api/tournaments/{id}/teams/{teamId}` | Registers an existing team in a tournament. |
| `POST` | `/api/tournaments/{id}/matches` | Schedules a match between two teams registered in the tournament. |
| `GET` | `/api/tournaments/{id}/standings` | Returns the current tournament standings. |

Game results are submitted through `POST /api/matches/{id}/games/results`. The request records an individual game's number, winning team and start/end times. The database procedure updates the match status and determines the series winner when enough games have been completed.

For example, tournament standings can be requested with:

```http
GET /api/tournaments/1/standings
```

### MySQL stored procedures, functions, triggers and views

The SQL initialization scripts in `data/database/init` create additional database objects:

- `sp_record_game_result` records a game result, validates it against the match format and updates the match result/status. It is called by the match result endpoint.
- `sp_get_tournament_standings` calculates standings for a tournament. It is called by the tournament standings endpoint. It can also be tested directly in MySQL:

  ```sql
  CALL sp_get_tournament_standings(1);
  ```
- `sp_save_team_membership` manages a player's membership period in a team.
- `sp_save_player_game_stats` saves player statistics for a game.
- `fn_team_match_win_rate` calculates a team's match win rate.
- `fn_team_game_win_rate` calculates a team's game win rate.
- `trg_matches_status_history` records changes to match status in `match_status_history`.
- `ev_refresh_player_statistics` refreshes the aggregated player statistics table daily. MySQL's event scheduler must be enabled for this event to run.
- `vw_player_leaderboard` and `vw_match_overview` provide read-only views for player statistics and match information.

The database definitions are in `01_gamehub_v7.sql`, `03_triggers.sql`, `04_events.sql` and `05_views.sql` under `data/database/init`.

## MongoDB migration

GameHub includes a one-time migration from the relational MySQL database to MongoDB. The migration reads the current MySQL data, maps relational entities into MongoDB documents and saves them in the `gamehub_documents` database. It does not continuously synchronize changes between MySQL and MongoDB. Run the migration again when you want to copy the current source records. Records deleted from MySQL are not automatically deleted from MongoDB by this migration, so it should not be treated as a continuously synchronized mirror.

The migration is activated with the Spring profile `migrate-mongo`. The migration copies the following data into MongoDB collections:

| MongoDB collection | Migrated data |
| --- | --- |
| `players` | Players, team membership history and aggregated player statistics. |
| `teams` | Teams. |
| `champions` | Champions and their roles. |
| `tournaments` | Tournaments and registered teams. |
| `matches` | Matches, games, player game statistics and match status history. |
| `users` | Application users. |

### 1. Configure MongoDB

Make sure the root `.env` file contains `MONGO_ROOT_USERNAME`, `MONGO_ROOT_PASSWORD` and `MONGODB_URI`. These values must match. For the default local setup, use `admin` as the username and replace `DIN_ADGANGSKODE` in the URI with the same password used for `MONGO_ROOT_PASSWORD`:

```properties
MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=your-local-password
MONGODB_URI=mongodb://admin:your-local-password@localhost:27017/gamehub_documents?authSource=admin
```

Use your own password instead of the example value. Keep `.env` out of Git. A simple alphanumeric password avoids needing to URL-encode special characters in the MongoDB connection URI.

### 2. Start MySQL and MongoDB

Run these commands from the project directory containing `docker-compose.yml`:

**Windows (PowerShell):**

```powershell
docker compose up -d gamehub_db mongodb
```

**macOS / Linux:**

```bash
docker compose up -d gamehub_db mongodb
```

MySQL must already be initialized and contain the data you want to migrate. On a new installation, the MySQL initialization scripts create and populate the database when the MySQL Docker volume is first created. MongoDB must also be running before starting the migration.

### 3. Run the migration

Run the command for your operating system from the project directory. Do not start the normal application at the same time. The migration profile activates `MigrationService`, which runs when Spring Boot starts.

**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"
```

**macOS / Linux:**

```bash
./mvnw spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"
```

Wait for the log message `Migration finished!`. If the process remains running, press `Ctrl+C` after the migration has finished. Then start the application normally using the command in [Local setup](#local-setup).

### 4. Verify the MongoDB data

The following command checks the number of player documents in MongoDB. It prompts for the MongoDB password. On a fresh installation, this count should match the MySQL player count:

**Windows (PowerShell):**

```powershell
docker compose exec mongodb mongosh gamehub_documents --username admin --authenticationDatabase admin --eval "db.players.countDocuments({})" --password
```

**macOS / Linux:**

```bash
docker compose exec mongodb mongosh gamehub_documents --username admin --authenticationDatabase admin --eval "db.players.countDocuments({})" --password
```

You can replace `players` in the query with `teams`, `champions`, `tournaments`, `matches` or `users` to check the other collections.

## Database Backup and Restore

The database can be backed up and verified/restored using the provided scripts. Run these commands from the project root directory using Git Bash.

1. Create a database backup

Run the following command to create a backup of the database:

```bash
$ bash scripts/backup-mysql.sh
```

The script creates a backup of the database and stores it in the backups directory.

2. Verify and restore a backup
To verify and test the restoration of a backup, run:
```bash
$ bash scripts/verify-backup.sh
```

The verification script automatically selects the newest backup from the backups directory and uses it to test the restore process.

Both scripts should be run through Git Bash.