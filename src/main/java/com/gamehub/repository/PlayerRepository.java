package com.gamehub.repository;

import com.gamehub.model.Player;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class PlayerRepository {

    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/gameHub";
    private final String DATABASE_USER = "root";
    private final String DATABASE_PASSWORD = "123456";

    public Optional<Player> findByNickname(String nickname) {
        String sql = "SELECT * FROM players WHERE nickname = '" + nickname + "'";

        try (
                Connection connection = DriverManager.getConnection(
                        DATABASE_URL,
                        DATABASE_USER,
                        DATABASE_PASSWORD
                );
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)
        ) {
            if (resultSet.next()) {
                Player player = new Player(
                        resultSet.getInt("id"),
                        resultSet.getString("nickname"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()
                );

                return Optional.of(player);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}