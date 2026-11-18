package com.gamehub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.Tournament;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    Optional<Tournament> findByName(String name);

    boolean existsByName(String name);
}
