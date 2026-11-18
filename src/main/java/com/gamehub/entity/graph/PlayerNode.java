package com.gamehub.entity.graph;

import java.time.LocalDateTime;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Player")
public record PlayerNode(@Id Long id, String nickname,
                         @Property("created_at") LocalDateTime createdAt) {
}
