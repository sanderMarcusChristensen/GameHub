package com.gamehub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.jpa.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Check for duplicate names when creating or updating a team.
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
