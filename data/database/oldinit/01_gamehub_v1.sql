-- GameHub: første relationelle model. MySQL 8.0.16+ (CHECK constraints).
-- Kør i en tom gamehub-database. Scriptet sletter eller migrerer ikke data.
-- Alle DATETIME-værdier indsættes som UTC.
CREATE DATABASE IF NOT EXISTS gamehub CHARACTER SET utf8mb4;
USE gamehub;

CREATE TABLE players (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NULL,
    ign VARCHAR(100) NOT NULL,
    nationality VARCHAR(100) NULL,
    date_of_birth DATE NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Ét aktuelt statistikøjebliksbillede pr. spiller.
-- sample_scope beskriver fx kamptype, periode eller 'seneste 50 kampe'.
-- NULL betyder ukendt, ikke nul. fetched_at er ikke statistikperioden.
CREATE TABLE player_stats (
    player_id INT NOT NULL,
    games_played INT UNSIGNED NULL,
    wins INT UNSIGNED NULL,
    losses INT UNSIGNED NULL,
    win_rate DECIMAL(7,6) NULL,
    avg_kills DECIMAL(10,4) NULL,
    avg_deaths DECIMAL(10,4) NULL,
    avg_assists DECIMAL(10,4) NULL,
    source VARCHAR(100) NOT NULL,
    sample_scope VARCHAR(255) NULL,
    fetched_at DATETIME NULL,
    is_synthetic BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (player_id),
    CONSTRAINT fk_stats_player FOREIGN KEY (player_id) REFERENCES players(id),
    CONSTRAINT chk_stats_rate CHECK (win_rate BETWEEN 0 AND 1),
    CONSTRAINT chk_stats_kills CHECK (avg_kills >= 0),
    CONSTRAINT chk_stats_deaths CHECK (avg_deaths >= 0),
    CONSTRAINT chk_stats_assists CHECK (avg_assists >= 0),
    CONSTRAINT chk_stats_synthetic CHECK (is_synthetic IN (0, 1))
) ENGINE = InnoDB;

CREATE TABLE champions (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(45) NULL,
    attack_range INT UNSIGNED NULL,
    release_date DATE NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_champion_name (name)
) ENGINE = InnoDB;

-- Ét aktuelt snapshot pr. spiller/champion; historik og flere scopes tilføjes senere.
-- Tallene må ikke blandes med andre kilder/scopes eller automatisk
-- overskrives af beregninger på en ufuldstændig lokal kamphistorik.
CREATE TABLE player_champion_stats (
    player_id INT NOT NULL,
    champion_id INT NOT NULL,
    games_played INT UNSIGNED NULL,
    wins INT UNSIGNED NULL,
    losses INT UNSIGNED NULL,
    win_rate DECIMAL(7,6) NULL,
    avg_kills DECIMAL(10,4) NULL,
    avg_deaths DECIMAL(10,4) NULL,
    avg_assists DECIMAL(10,4) NULL,
    source VARCHAR(100) NOT NULL,
    sample_scope VARCHAR(255) NULL,
    fetched_at DATETIME NULL,
    is_synthetic BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (player_id, champion_id),
    CONSTRAINT fk_pcs_player FOREIGN KEY (player_id) REFERENCES players(id),
    CONSTRAINT fk_pcs_champion FOREIGN KEY (champion_id) REFERENCES champions(id),
    CONSTRAINT chk_pcs_win_rate CHECK (win_rate BETWEEN 0 AND 1),
    CONSTRAINT chk_pcs_kills CHECK (avg_kills >= 0),
    CONSTRAINT chk_pcs_deaths CHECK (avg_deaths >= 0),
    CONSTRAINT chk_pcs_assists CHECK (avg_assists >= 0),
    CONSTRAINT chk_pcs_synthetic CHECK (is_synthetic IN (0, 1))
) ENGINE = InnoDB;

CREATE TABLE teams (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(45) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- Et selvstændigt id tillader, at en spiller vender tilbage til samme hold.
-- Overlappende medlemskaber håndteres senere som en forretningsregel.
CREATE TABLE player_teams (
    id INT NOT NULL AUTO_INCREMENT,
    player_id INT NOT NULL,
    team_id INT NOT NULL,
    joined_at DATE NULL,
    left_at DATE NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pt_player FOREIGN KEY (player_id) REFERENCES players(id),
    CONSTRAINT fk_pt_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT chk_pt_dates CHECK (left_at IS NULL OR joined_at IS NULL OR left_at >= joined_at)
) ENGINE = InnoDB;

-- Én række er ét spil, ikke en bedst-af-fem-serie.
-- Denne første udgave modellerer kampe mellem navngivne hold.
CREATE TABLE matches (
    id INT NOT NULL AUTO_INCREMENT,
    team1_id INT NOT NULL,
    team2_id INT NOT NULL,
    winner_team_id INT NULL,
    played_at DATETIME NOT NULL,
    source VARCHAR(100) NOT NULL,
    external_id VARCHAR(255) NULL,
    is_synthetic BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    UNIQUE KEY uq_match_source_id (source, external_id),
    KEY idx_matches_played_at (played_at),
    CONSTRAINT fk_match_team1 FOREIGN KEY (team1_id) REFERENCES teams(id),
    CONSTRAINT fk_match_team2 FOREIGN KEY (team2_id) REFERENCES teams(id),
    CONSTRAINT fk_match_winner FOREIGN KEY (winner_team_id) REFERENCES teams(id),
    CONSTRAINT chk_match_teams CHECK (team1_id <> team2_id),
    CONSTRAINT chk_match_winner CHECK (
        winner_team_id IS NULL OR winner_team_id IN (team1_id, team2_id)
    ),
    CONSTRAINT chk_match_synthetic CHECK (is_synthetic IN (0, 1))
) ENGINE = InnoDB;

-- Champion-valget og præstationen i det konkrete spil.
-- team_side=1 betyder matches.team1_id; team_side=2 betyder matches.team2_id.
-- Så kan en deltager ikke knyttes til et tredje hold uden for kampen.
CREATE TABLE match_participants (
    match_id INT NOT NULL,
    player_id INT NOT NULL,
    champion_id INT NOT NULL,
    team_side TINYINT NOT NULL,
    kills INT UNSIGNED NULL,
    deaths INT UNSIGNED NULL,
    assists INT UNSIGNED NULL,
    PRIMARY KEY (match_id, player_id),
    KEY idx_participant_player_champion (player_id, champion_id),
    CONSTRAINT fk_mp_match FOREIGN KEY (match_id) REFERENCES matches(id),
    CONSTRAINT fk_mp_player FOREIGN KEY (player_id) REFERENCES players(id),
    CONSTRAINT fk_mp_champion FOREIGN KEY (champion_id) REFERENCES champions(id),
    CONSTRAINT chk_mp_side CHECK (team_side IN (1, 2))
) ENGINE = InnoDB;

-- Standardreglen RESTRICT/NO ACTION bevarer refererede historiske data.
-- Modellen håndhæver endnu ikke præcis fem deltagere på hver side.
