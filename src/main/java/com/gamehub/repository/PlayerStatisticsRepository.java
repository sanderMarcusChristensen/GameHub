package com.gamehub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamehub.entity.PlayerStatistics;

public interface PlayerStatisticsRepository
        extends JpaRepository<PlayerStatistics, Long> {

    List<PlayerStatistics> findAllByOrderByWinRateDesc();

    List<PlayerStatistics> findByWinRateGreaterThanEqualOrderByWinRateDesc(
            java.math.BigDecimal minimumWinRate
    );
}

