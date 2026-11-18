package com.gamehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.Champion;

public interface ChampionRepository extends JpaRepository<Champion, Long> {

    Optional<Champion> findByName(String name);

    boolean existsByName(String name);
}
