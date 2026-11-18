package com.gamehub.repository;

import com.gamehub.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByNickname(String nickname);

    Optional<Player> findByNickname(String nickname);
}