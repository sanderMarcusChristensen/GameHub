
package com.gamehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "champions")
public class Champion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "released_at", nullable = false)
    private LocalDate releasedAt;

    @ManyToMany
    @JoinTable(
            name = "champions_has_champion_roles",
            joinColumns = @JoinColumn(name = "champions_id"),
            inverseJoinColumns = @JoinColumn(name = "champion_roles_id")
    )
    private Set<ChampionRole> roles = new HashSet<>();

    public Champion(String name, LocalDate releasedAt) {
        this.name = name;
        this.releasedAt = releasedAt;
    }

    public void addRole(ChampionRole role) {
        roles.add(role);
        role.getChampions().add(this);
    }
}
