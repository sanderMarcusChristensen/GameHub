package com.gamehub.graphrepository;

import com.gamehub.dto.PlayerTeamHistory;
import java.util.Optional;

public interface PlayerTeamHistoryReader {
    Optional<PlayerTeamHistory> findTeamHistory(long playerId);
}
