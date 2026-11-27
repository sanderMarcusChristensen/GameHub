# GameHub project setup guide

Use this guide to run GameHub on a new computer. The Spring Boot backend runs on your computer. Docker runs MySQL, MongoDB and Neo4j. On the first run, MySQL loads the sample data; migrating the data from MySQL to MongoDB and Neo4j is optional.

## 1. Check your tools

GameHub needs Git, a full JDK 21 and Docker — Docker Desktop on Windows or macOS, Docker Engine on Linux. Start Docker before continuing, then open PowerShell on Windows or Terminal on macOS and Linux. Check that the following commands work. Both Java commands should report version 21, and `docker info` should show a Server section.

```sh
git --version
java -version
javac -version
docker compose version
docker info
```

If a command is missing or fails, install the tool first: [Git](https://git-scm.com/downloads), [JDK 21 (Temurin)](https://adoptium.net/temurin/releases?version=21&os=any&arch=any) or [Docker](https://docs.docker.com/get-docker/). Both Docker Desktop and Docker Engine include Compose v2.

A full JDK provides both `java` (the runtime and launcher) and `javac` (the compiler). If `java` works but `javac` does not, you installed a JRE rather than a JDK.

GameHub includes a Maven wrapper. Its first run needs an internet connection to download Maven and project dependencies.

## 2. Clone the project and create your local settings

Choose your operating system below. If you already have a checkout, open its directory and keep your existing `.env`. Run the remaining commands in the directory containing `pom.xml` and `docker-compose.yml`.

### Windows (PowerShell)

```powershell
git clone https://github.com/sanderMarcusChristensen/GameHub.git
cd GameHub
Copy-Item .env.example .env
```

### macOS / Linux (Terminal)

```sh
git clone https://github.com/sanderMarcusChristensen/GameHub.git
cd GameHub
cp .env.example .env
```

## 3. Fill in the .env file

Use [.env.example](.env.example) as your checklist. Edit the copy named `.env` and save it before starting Docker. Use one plain `KEY=value` per line. Keep passwords and API keys in your local copy.

| Setting | Value for a fresh local setup |
| --- | --- |
| MySQL connection | Set `MYSQL_HOST=127.0.0.1`, **`MYSQL_PORT=3307`** and `MYSQL_DATABASE=gameHub`. Docker maps port 3307 on your computer to port 3306 in the container. Set `MYSQL_ROOT_PASSWORD` to your own password. Leave the four database-user credentials at their `.env.example` values — they are fixed local demo passwords that must match the SQL script. The backend connects with the app account. |
| MongoDB connection | Keep `MONGO_ROOT_USERNAME=admin` and set `MONGO_ROOT_PASSWORD`. In `MONGODB_URI`, replace `DIN_ADGANGSKODE` with that same password. Keep `localhost:27017/gamehub_documents?authSource=admin`. A long random alphanumeric password can be copied directly into the URI. |
| Neo4j connection | The local defaults are user `neo4j`, password `gamehub-local-demo` and driver address `bolt://localhost:7687`. To choose your own password, add `NEO4J_PASSWORD` to `.env` before the first startup. |
| External player statistics | For live player lookups, create an account at [CitoAPI](https://citoapi.com/), generate an API key in the dashboard and set `CITO_API_KEY`. The example value is enough to start the local database features. |
| Browser login | Keep `SESSION_COOKIE_SECURE=false` for local HTTP. |

### Database accounts (background)

On first startup Docker creates four accounts from [data/database/init/06_users_and_privileges.sql](data/database/init/06_users_and_privileges.sql): `gamehub_app` (CRUD and stored routines only, used by the backend), `gamehub_admin` (full access to `gameHub`), `gamehub_readonly` (SELECT on everything) and `gamehub_restricted` (SELECT on domain data, but no access to `email` or `password_hash` in `app_users`). The MySQL image does not expand environment variables in `.sql` files, so those passwords are written literally in the script and mirrored in `.env`; they are local demo values, not for production. Accounts are only created when the MySQL volume is empty. If you change a password, update both files and remove the `gamehub_gamehub_db_data` volume before starting again (this deletes the local MySQL data).

## 4. Start the databases

These Docker commands work on Windows, macOS and Linux.

```sh
docker compose up -d
docker compose ps
```

On a fresh MySQL volume, Docker runs the scripts in [data/database/init](data/database/init) to create the schema, load sample data and create the MySQL accounts. Give it time to finish before starting the backend. If startup fails, check `docker compose logs --tail=50 gamehub_db`. An existing volume keeps its original data and credentials, including the passwords from the first run.

## 5. Run the backend

Run the command for your operating system. Leave this terminal open while using the application.

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run
```

### macOS / Linux (Terminal)

```sh
./mvnw spring-boot:run
```

## 6. Migrate the MySQL data to MongoDB and Neo4j (optional)

GameHub works with MySQL alone. If you also want the document and graph databases, stop the backend with **Ctrl+C**, then run the two commands in your operating system's block **one at a time**.

1. The first command migrates MySQL data to Neo4j. Wait for `Neo4j migration completed and verified.` and the terminal prompt.
2. The second migrates MySQL data to MongoDB. Wait for `Migration finished!`. If the server is still running, press **Ctrl+C**.

Afterwards, start the backend again with the command from step 5.

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=migrate-neo4j"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=migrate-mongo"
```

### macOS / Linux (Terminal)

```sh
./mvnw spring-boot:run "-Dspring-boot.run.profiles=migrate-neo4j"
./mvnw spring-boot:run "-Dspring-boot.run.profiles=migrate-mongo"
```

## 7. Check the installation

- **GameHub and MySQL.** Open the [login page](http://localhost:8080/login.html), select **Create account**, register with an email and a password of at least eight characters, then sign in. Open the [player list](http://localhost:8080/api/players) in the same browser. The response should contain player records. Open the [home page](http://localhost:8080/) to use the frontend.
- **Neo4j (after the optional migration).** Open [Neo4j Browser](http://localhost:7474), enter your Neo4j credentials and run `MATCH p = (:Player)-[:MEMBER_OF]->(:Team) RETURN p LIMIT 10`. The sample data should show players connected to teams. In the browser where you signed in to GameHub, open [player 9's team history](http://localhost:8080/api/graph/players/9/teams) to check the backend connection. Use a current player ID if you changed the sample data.
- **MongoDB (after the optional migration).** The [MongoDB migration section](README.md#mongodb-migration) in the README shows how to inspect the migrated collections with `mongosh`.

## 8. Stop and restart

Press **Ctrl+C** in the backend terminal and run `docker compose stop`. To resume later, run `docker compose start`, wait for the databases, and run the command from step 5. The Docker volumes keep the data.

For more detail on the graph migration and optional test cleanup, see the [Neo4j local setup guide](doc/neo4j/local-setup.md).
