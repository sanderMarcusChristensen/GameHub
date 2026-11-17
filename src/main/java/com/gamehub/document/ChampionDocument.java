package com.gamehub.document;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "champions")
public class ChampionDocument {

    @Id
    private Long id;

    private String name;
    private LocalDate releasedAt;

    private List<ChampionRole> roles;

    public ChampionDocument() {
    }

    public ChampionDocument(Long id, String name, LocalDate releasedAt,
                            List<ChampionRole> roles) {
        this.id = id;
        this.name = name;
        this.releasedAt = releasedAt;
        this.roles = roles;
    }

    public static class ChampionRole {

        private Long roleId;
        private String name;

        public ChampionRole() {
        }

        public ChampionRole(Long roleId, String name) {
            this.roleId = roleId;
            this.name = name;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
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

    public LocalDate getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(LocalDate releasedAt) {
        this.releasedAt = releasedAt;
    }

    public List<ChampionRole> getRoles() {
        return roles;
    }

    public void setRoles(List<ChampionRole> roles) {
        this.roles = roles;
    }
}
