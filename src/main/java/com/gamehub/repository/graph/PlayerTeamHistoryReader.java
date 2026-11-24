package com.gamehub.repository.graph;

import com.gamehub.dto.PlayerTeamHistory;
import java.util.Optional;

public interface PlayerTeamHistoryReader {
    Optional<PlayerTeamHistory> findTeamHistory(long playerId);
}
