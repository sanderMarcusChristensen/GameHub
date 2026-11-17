package com.gamehub.entity.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "teams")
public class TeamDocument {

    @Id
    private Long id;

    private String name;
    private String shortName;
    private LocalDateTime createdAt;

    public TeamDocument() {
    }

    public TeamDocument(Long id, String name, String shortName, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
