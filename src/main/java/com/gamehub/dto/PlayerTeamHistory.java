package com.gamehub.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PlayerTeamHistory(long playerId, String nickname, List<Membership> memberships) {
    public record Membership(long teamId, String teamName, LocalDateTime joinedAt, LocalDateTime leftAt) {}
}
