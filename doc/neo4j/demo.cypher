// Independent demonstration: these labels do not overlap with migrated data.
MERGE (p:DemoPlayer {nickname: 'Example player'})
MERGE (t:DemoTeam {name: 'Example team'})
MERGE (p)-[membership:DEMO_MEMBER_OF {joined_at: date('2024-01-01')}]->(t)
RETURN p, membership, t;

// Run separately in Neo4j Browser to find the membership.
MATCH (p:DemoPlayer)-[membership:DEMO_MEMBER_OF]->(t:DemoTeam)
RETURN p, membership, t;
