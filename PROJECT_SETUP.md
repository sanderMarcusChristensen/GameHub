# GameHub project setup guide

Use this guide to run GameHub on a new computer. The Spring Boot backend runs on your computer. Docker runs MySQL, MongoDB and Neo4j. On the first run, MySQL loads the sample data and you run two commands to copy it into MongoDB and Neo4j.

## 1. Check your tools

Install Git, JDK 21 and Docker with Compose v2. Start Docker Desktop on Windows or macOS, or the Docker service on Linux. Open PowerShell on Windows or Terminal on macOS and Linux. Check that the following commands work. Both Java commands should report version 21, and `docker info` should show a Server section.

```text
git --version
java -version
javac -version
docker compose version
docker info
```

GameHub includes a Maven wrapper. Its first run needs an internet connection to download Maven and project dependencies.

## 2. Clone the project and create your local settings

Choose your operating system below. If you already have a checkout, open its directory and keep your existing `.env`. Run the remaining commands in the directory containing `pom.xml` and `docker-compose.yml`.

### Windows (PowerShell)

```powershell
git clone https://github.com/sanderMarcusChristensen/GameHub.git
cd GameHub
Copy-Item .env.example .env
notepad .env
```

### macOS (Terminal)

```sh
git clone https://github.com/sanderMarcusChristensen/GameHub.git
cd GameHub
cp .env.example .env
open -e .env
```

### Linux (Terminal)

```sh
git clone https://github.com/sanderMarcusChristensen/GameHub.git
cd GameHub
cp .env.example .env
nano .env
```

## 3. Fill in the .env file

Use [.env.example](.env.example) as your checklist. Edit the copy named `.env` and save it before starting Docker. Use one plain `KEY=value` per line. Keep passwords and API keys in your local copy.

| Setting | Value for a fresh local setup |
| --- | --- |
| MySQL connection | Set `DB_HOST=127.0.0.1`, **`DB_PORT=3307`**, `DB_NAME=gameHub` and `DB_USERNAME=root`. Docker maps port 3307 on your computer to port 3306 in the container. Set `DB_PASSWORD` to your chosen local password. |
| MongoDB connection | Keep `MONGO_ROOT_USERNAME=admin` and set `MONGO_ROOT_PASSWORD`. In `MONGODB_URI`, replace `DIN_ADGANGSKODE` with that same password. Keep `localhost:27017/gamehub_documents?authSource=admin`. A long random alphanumeric password can be copied directly into the URI. |
| Neo4j connection | The local defaults are user `neo4j`, password `gamehub-local-demo` and driver address `bolt://localhost:7687`. To choose your own password, add `NEO4J_PASSWORD` to `.env` before the first startup. |
| External player statistics | For live player lookups, create an account at [CitoAPI](https://citoapi.com/), generate an API key in the dashboard and set `CITO_API_KEY`. The example value is enough to start the local database features. |
| Browser login | Keep `SESSION_COOKIE_SECURE=false` for local HTTP. |

## 4. Start the databases and check the sample data

These Docker commands work on all three systems.

```sh
docker compose up -d gamehub_db mongodb neo4j
docker compose ps
```

On a fresh MySQL volume, Docker runs the scripts in [data/database/init](data/database/init) to create the schema and load sample data. Give it time to finish, then run this query and enter your `DB_PASSWORD` when prompted.

```sh
docker compose exec gamehub_db mysql -u root -p gameHub -e "SELECT COUNT(*) AS source_players FROM players;"
```

You should see a positive player count. Save the number for step 6. If the connection fails during startup, check `docker compose logs --tail=50 gamehub_db` and retry after initialization finishes. An existing volume keeps its original data and credentials.

## 5. Import the data and start the backend

Run the commands in your operating system's block **one at a time**.

1. The first command imports MySQL data into Neo4j. Wait for `Neo4j migration completed and verified.` and the terminal prompt.
2. The second starts GameHub with the MongoDB migration profile. Wait for `Migration finished!`. If the server is still running, press **Ctrl+C** before the next command.
3. The third starts GameHub normally. Leave this terminal open while using the application.

### Windows (PowerShell)

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=migrate-neo4j"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication"
```

### macOS (Terminal)

```sh
./mvnw spring-boot:run "-Dspring-boot.run.profiles=migrate-neo4j"
./mvnw spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"
./mvnw spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication"
```

### Linux (Terminal)

```sh
./mvnw spring-boot:run "-Dspring-boot.run.profiles=migrate-neo4j"
./mvnw spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication" "-Dspring-boot.run.profiles=migrate-mongo"
./mvnw spring-boot:run "-Dspring-boot.run.main-class=com.gamehub.GameHubApplication"
```

## 6. Check the installation

- **GameHub and MySQL.** Open the [login page](http://localhost:8080/login.html), select **Create account**, register with an email and a password of at least eight characters, then sign in. Open the [player list](http://localhost:8080/api/players) in the same browser. The response should contain player records. Open the [home page](http://localhost:8080/) to use the frontend.
- **Neo4j.** Open [Neo4j Browser](http://localhost:7474), enter your Neo4j credentials and run `MATCH p = (:Player)-[:MEMBER_OF]->(:Team) RETURN p LIMIT 10`. The sample data should show players connected to teams. In the browser where you signed in to GameHub, open [player 9's team history](http://localhost:8080/api/graph/players/9/teams) to check the backend connection. Use a current player ID if you changed the sample data.
- **MongoDB.** In a second terminal, open the project directory and run the command below. Enter `MONGO_ROOT_PASSWORD` when prompted. On a fresh installation, the result should match the MySQL count from step 4.

```sh
docker compose exec mongodb mongosh gamehub_documents --username admin --authenticationDatabase admin --eval "db.players.countDocuments({})" --password
```

## 7. Stop and restart

Press **Ctrl+C** in the backend terminal and run `docker compose stop`. To resume later, run `docker compose start`, wait for the databases, and run the **third** command from step 5. The Docker volumes keep the imported data.

For more detail on the graph import and optional test cleanup, see the [Neo4j local setup guide](doc/neo4j/local-setup.md).
