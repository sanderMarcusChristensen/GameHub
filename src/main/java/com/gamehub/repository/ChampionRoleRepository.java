package com.gamehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.ChampionRole;

public interface ChampionRoleRepository extends JpaRepository<ChampionRole, Integer> {

    Optional<ChampionRole> findByName(String name);

    boolean existsByName(String name);
}
