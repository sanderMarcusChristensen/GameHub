package com.gamehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "champion_roles")
public class ChampionRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 45)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Champion> champions = new HashSet<>();

    public ChampionRole(String name) {
        this.name = name;
    }
}
