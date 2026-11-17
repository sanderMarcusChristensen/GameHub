# Local Neo4j demo

From the project root, start only Neo4j:

```sh
docker compose -f docker-compose.neo4j.yml up -d
```

Open http://localhost:7474, connect to `bolt://localhost:7687` and log in with username `neo4j` and password `gamehub-local-demo`. This is a local development password; to choose your own before the first start, set `NEO4J_PASSWORD` in the ignored root `.env` file. The initial password only applies to a new database volume; changing the environment variable does not change an existing database password.

Copy the first query in `demo.cypher` into Neo4j Browser and run it. It creates two example nodes and a membership relationship. Run the second query to retrieve them. The demo uses separate labels and a relationship type so it is not confused with migrated MySQL data. `MERGE` makes rerunning this example safe.

To stop the container while preserving its data:

```sh
docker compose -f docker-compose.neo4j.yml stop
```

The named volume persists data between starts. This demo does not migrate MySQL data.
