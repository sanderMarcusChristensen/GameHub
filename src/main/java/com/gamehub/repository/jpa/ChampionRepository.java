package com.gamehub.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamehub.entity.jpa.Champion;

public interface ChampionRepository extends JpaRepository<Champion, Long> {

    Optional<Champion> findByName(String name);

    boolean existsByName(String name);
    
    //added only for migration purposes, ensuring that the roles are loaded together with the champions
    @Query("SELECT DISTINCT c FROM Champion c LEFT JOIN FETCH c.roles")
    List<Champion> findAllWithRoles();

}
