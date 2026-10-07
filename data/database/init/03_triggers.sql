USE lol_esports;

-- Keeps an audit trail when a match moves between lifecycle states.
-- The trigger runs for every SQL client, including the future Java application.
CREATE TABLE match_status_history (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    match_id BIGINT UNSIGNED NOT NULL,
    old_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_match_status_history_match (match_id),
    CONSTRAINT fk_match_status_history_match
        FOREIGN KEY (match_id) REFERENCES matches(id)
) ENGINE = InnoDB;

DELIMITER $$

CREATE TRIGGER trg_matches_status_history
AFTER UPDATE ON matches
FOR EACH ROW
BEGIN
    IF NOT (OLD.status <=> NEW.status) THEN
        INSERT INTO match_status_history (
            match_id,
            old_status,
            new_status
        )
        VALUES (
            NEW.id,
            OLD.status,
            NEW.status
        );
    END IF;
END$$

DELIMITER ;