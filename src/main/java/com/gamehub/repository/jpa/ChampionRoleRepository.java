package com.gamehub.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.jpa.ChampionRole;

public interface ChampionRoleRepository extends JpaRepository<ChampionRole, Integer> {

    Optional<ChampionRole> findByName(String name);

    boolean existsByName(String name);
}
