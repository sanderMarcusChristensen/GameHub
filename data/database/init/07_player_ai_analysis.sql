USE gameHub;

CREATE TABLE IF NOT EXISTS player_ai_analysis (
    player_id BIGINT UNSIGNED NOT NULL,
    team_id BIGINT UNSIGNED NOT NULL,
    question VARCHAR(1000) NOT NULL,
    analysis TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (player_id, team_id),
    CONSTRAINT fk_ai_analysis_player FOREIGN KEY (player_id) REFERENCES players (id),
    CONSTRAINT fk_ai_analysis_team FOREIGN KEY (team_id) REFERENCES teams (id)
) ENGINE = InnoDB;
