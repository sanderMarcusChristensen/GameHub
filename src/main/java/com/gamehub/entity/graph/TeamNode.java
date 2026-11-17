package com.gamehub.entity.graph;

import java.time.LocalDateTime;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Team")
public record TeamNode(@Id Long id, String name,
                       @Property("short_name") String shortName,
                       @Property("created_at") LocalDateTime createdAt) {
}
