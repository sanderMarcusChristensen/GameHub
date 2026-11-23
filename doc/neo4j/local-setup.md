# Kør og test Neo4j lokalt

Kør kommandoerne fra GameHub-projektets rod. Brug projektets Java 21, Maven-wrapper og Docker Compose.

## 1. Start databaserne

```sh
docker compose up -d gamehub_db neo4j
docker compose ps
```

Brug gruppens eksisterende MySQL-database med dummydata. Ved lokal Docker Compose skal projektets `.env` pege på den publicerede MySQL-port (`DB_PORT=3307`), databasen `gameHub` og den tilhørende bruger og adgangskode. Neo4j Browser ligger på <http://localhost:7474>, og driveren bruger `http://localhost:7474/browser/`. Standardbrugeren er `neo4j`. Den lokale standardkode er `gamehub-local-demo`. Hvis projektet har sat `NEO4J_PASSWORD`, bruges den adgangskode, volumenet blev oprettet med.

## 2. Importér

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=migrate-neo4j
```

Ved succes vises antal verificerede rækker pr. tabel og til sidst `Neo4j migration completed and verified.`. Hvis en kørsel fx melder `Player: expected 12 matches, found 13`, findes der flere Player-noder i målet end forventet fra kilden. Kontrollér de eksisterende noder i Browser. Importens dataændringer er rullet tilbage.

## 3. Se resultatet

Kør følgende i Neo4j Browser for at se medlemskaberne som en graf:

```cypher
MATCH path = (:Player)-[:MEMBER_OF]->(:Team)
RETURN path
```

For at teste gennem Java skal den almindelige GameHub-backend køre. Start den om nødvendigt:

```sh
./mvnw spring-boot:run -Dspring-boot.run.main-class=com.gamehub.GameHubApplication
```

Log ind på GameHub og åbn <http://localhost:8080/api/graph/players/9/teams> i samme browser. I det testede datasæt har Raven id 9. Brug det aktuelle spiller-id, hvis kildedataene har ændret sig.

## 4. Ryd grafen før en ny test

```sh
./scripts/clear-neo4j.sh
```

Kontrollér destinationen, de viste labels og antallene. Tryk Enter for at annullere, eller skriv `CLEAR` for at slette. Kommandoen rammer **alle noder med GameHubs labels**, også manuelt oprettede testnoder. Den fjerner de mappede relationer og noder i én transaktion. En anden relation til en af noderne får rydningen til at rulle tilbage. MySQL-data og grafens unikhedsregler bevares. Vent på afslutning, og kør så importkommandoen fra trin 2.

Stop om nødvendigt Neo4j med `docker compose stop neo4j`. Data gemmes i projektets Docker-volumen.
