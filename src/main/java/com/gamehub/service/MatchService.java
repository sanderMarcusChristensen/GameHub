
package com.gamehub.service;

import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.gamehub.dto.GameResultRequest;

@Service
public class MatchService {

    private final JdbcTemplate jdbcTemplate;

    public MatchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   
public void recordGameResult(Long matchId, GameResultRequest request) {

    jdbcTemplate.execute(
            (CallableStatementCreator) connection -> {
                var statement = connection.prepareCall(
                        "{CALL sp_record_game_result(?, ?, ?, ?, ?)}"
                );

                statement.setLong(1, matchId);
                statement.setInt(2, request.gameNumber());
                statement.setLong(3, request.winnerTeamId());
                statement.setObject(4, request.startedAt());
                statement.setObject(5, request.endedAt());

                return statement;
            },
            (CallableStatementCallback<Void>) statement -> {
                statement.execute();
                return null;
            }
    );
}

}
