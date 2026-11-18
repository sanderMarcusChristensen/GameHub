package com.gamehub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByMatch_Id(Long matchId);

    Optional<Game> findByMatch_IdAndGameNumber(
            Long matchId,
            Integer gameNumber
    );

    boolean existsByMatch_IdAndGameNumber(
            Long matchId,
            Integer gameNumber
    );
}
