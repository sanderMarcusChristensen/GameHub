package com.gamehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.MatchStatusHistory;

public interface MatchStatusHistoryRepository
        extends JpaRepository<MatchStatusHistory, Long> {

    List<MatchStatusHistory> findByMatch_IdOrderByChangedAtAsc(
            Long matchId
    );
}
