package com.gamehub.repository;

import com.gamehub.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Check for duplicate names when creating or updating a team.
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
