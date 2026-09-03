-- TechCup Futbol - relational schema (PostgreSQL 16).
-- Enumerations are VARCHAR columns guarded by CHECK constraints and mapped with
-- @Enumerated(EnumType.STRING). All timestamps are TIMESTAMPTZ.

-- ---------------------------------------------------------------------------
-- identity
-- ---------------------------------------------------------------------------
CREATE TABLE users (
    id               BIGSERIAL PRIMARY KEY,
    full_name        VARCHAR(150) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    password_hash    VARCHAR(100) NOT NULL,
    -- NULL only for referees (created by an organizer, no school relation captured)
    school_relation  VARCHAR(20)
        CHECK (school_relation IN ('STUDENT', 'PROFESSOR', 'ADMINISTRATIVE', 'GRADUATE', 'FAMILY')),
    academic_program VARCHAR(40)
        CHECK (academic_program IN ('SYSTEMS_ENGINEERING', 'AI_ENGINEERING', 'CYBERSECURITY_ENGINEERING',
                                    'STATISTICS_ENGINEERING', 'OTHER')),
    semester         INT CHECK (semester IS NULL OR semester BETWEEN 1 AND 20),
    status           VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    birth_date       DATE NOT NULL,
    document_type    VARCHAR(10) NOT NULL CHECK (document_type IN ('CC', 'TI', 'CE', 'PASSPORT')),
    document_number  VARCHAR(30) NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_document UNIQUE (document_type, document_number)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role    VARCHAR(20) NOT NULL
        CHECK (role IN ('GUEST', 'PLAYER', 'CAPTAIN', 'ORGANIZER', 'REFEREE', 'ADMIN')),
    PRIMARY KEY (user_id, role)
);

-- ---------------------------------------------------------------------------
-- shared / audit
-- ---------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id            BIGSERIAL PRIMARY KEY,
    actor_user_id BIGINT REFERENCES users (id),
    action        VARCHAR(50) NOT NULL,
    entity_type   VARCHAR(50),
    entity_id     BIGINT,
    details       JSONB,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX ix_audit_logs_created_at ON audit_logs (created_at DESC);
CREATE INDEX ix_audit_logs_action ON audit_logs (action);

-- ---------------------------------------------------------------------------
-- players
-- ---------------------------------------------------------------------------
CREATE TABLE player_profiles (
    user_id       BIGINT PRIMARY KEY REFERENCES users (id),
    position      VARCHAR(20) NOT NULL
        CHECK (position IN ('GOALKEEPER', 'DEFENDER', 'MIDFIELDER', 'FORWARD')),
    jersey_number INT NOT NULL CHECK (jersey_number BETWEEN 1 AND 99),
    photo_file_id VARCHAR(64),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------------
-- teams
-- ---------------------------------------------------------------------------
CREATE TABLE teams (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL UNIQUE,
    colors          VARCHAR(100) NOT NULL,
    captain_user_id BIGINT NOT NULL REFERENCES users (id),
    status          VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- The captain is also a member. The application layer guarantees that a user
-- belongs to at most one ACTIVE team.
CREATE TABLE team_members (
    team_id   BIGINT NOT NULL REFERENCES teams (id) ON DELETE CASCADE,
    user_id   BIGINT NOT NULL REFERENCES users (id),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (team_id, user_id)
);
CREATE INDEX ix_team_members_user ON team_members (user_id);

-- The application layer guarantees at most ONE PENDING request per player.
CREATE TABLE join_requests (
    id             BIGSERIAL PRIMARY KEY,
    team_id        BIGINT NOT NULL REFERENCES teams (id) ON DELETE CASCADE,
    player_user_id BIGINT NOT NULL REFERENCES users (id),
    status         VARCHAR(10) NOT NULL
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
    message        VARCHAR(500),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at    TIMESTAMPTZ
);
CREATE INDEX ix_join_requests_player ON join_requests (player_user_id);
CREATE INDEX ix_join_requests_team_status ON join_requests (team_id, status);

-- ---------------------------------------------------------------------------
-- tournaments
-- ---------------------------------------------------------------------------
CREATE TABLE tournaments (
    id                    BIGSERIAL PRIMARY KEY,
    name                  VARCHAR(150) NOT NULL,
    start_date            DATE NOT NULL,
    end_date              DATE NOT NULL,
    registration_deadline DATE NOT NULL,
    max_teams             INT NOT NULL CHECK (max_teams >= 2),
    fee                   NUMERIC(12, 2) NOT NULL CHECK (fee >= 0),
    status                VARCHAR(15) NOT NULL
        CHECK (status IN ('DRAFT', 'ACTIVE', 'IN_PROGRESS', 'FINISHED')),
    rulebook_file_id      VARCHAR(64),
    created_by            BIGINT NOT NULL REFERENCES users (id),
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE venues (
    id            BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournaments (id) ON DELETE CASCADE,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(500),
    image_file_id VARCHAR(64)
);
CREATE INDEX ix_venues_tournament ON venues (tournament_id);

CREATE TABLE registrations (
    id              BIGSERIAL PRIMARY KEY,
    tournament_id   BIGINT NOT NULL REFERENCES tournaments (id) ON DELETE CASCADE,
    team_id         BIGINT NOT NULL REFERENCES teams (id),
    receipt_file_id VARCHAR(64) NOT NULL,
    status          VARCHAR(15) NOT NULL
        CHECK (status IN ('UNDER_REVIEW', 'APPROVED', 'REJECTED', 'CANCELLED')),
    review_note     VARCHAR(500),
    reviewed_by     BIGINT REFERENCES users (id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    reviewed_at     TIMESTAMPTZ
);
-- A team can hold at most one live (under review or approved) registration per
-- tournament; rejected/cancelled rows stay as history and allow a new attempt.
CREATE UNIQUE INDEX uk_registrations_live
    ON registrations (tournament_id, team_id)
    WHERE status IN ('UNDER_REVIEW', 'APPROVED');
CREATE INDEX ix_registrations_team_status ON registrations (team_id, status);

-- ---------------------------------------------------------------------------
-- competition
-- ---------------------------------------------------------------------------
CREATE TABLE matches (
    id              BIGSERIAL PRIMARY KEY,
    tournament_id   BIGINT NOT NULL REFERENCES tournaments (id) ON DELETE CASCADE,
    phase           VARCHAR(15) NOT NULL
        CHECK (phase IN ('GROUP', 'QUARTERFINAL', 'SEMIFINAL', 'FINAL')),
    round_number    INT NOT NULL,
    home_team_id    BIGINT NOT NULL REFERENCES teams (id),
    away_team_id    BIGINT NOT NULL REFERENCES teams (id),
    venue_id        BIGINT REFERENCES venues (id) ON DELETE SET NULL,
    referee_user_id BIGINT REFERENCES users (id),
    scheduled_at    TIMESTAMPTZ,
    status          VARCHAR(10) NOT NULL CHECK (status IN ('SCHEDULED', 'PLAYED', 'CANCELLED')),
    home_score      INT CHECK (home_score IS NULL OR home_score >= 0),
    away_score      INT CHECK (away_score IS NULL OR away_score >= 0),
    home_penalties  INT CHECK (home_penalties IS NULL OR home_penalties >= 0),
    away_penalties  INT CHECK (away_penalties IS NULL OR away_penalties >= 0),
    cancel_reason   VARCHAR(15) CHECK (cancel_reason IS NULL OR cancel_reason IN ('DISQUALIFIED', 'NO_SHOW')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_matches_distinct_teams CHECK (home_team_id <> away_team_id)
);
CREATE INDEX ix_matches_tournament_phase ON matches (tournament_id, phase);
CREATE INDEX ix_matches_referee ON matches (referee_user_id);

CREATE TABLE match_events (
    id             BIGSERIAL PRIMARY KEY,
    match_id       BIGINT NOT NULL REFERENCES matches (id) ON DELETE CASCADE,
    team_id        BIGINT NOT NULL REFERENCES teams (id),
    player_user_id BIGINT NOT NULL REFERENCES users (id),
    type           VARCHAR(15) NOT NULL CHECK (type IN ('GOAL', 'YELLOW_CARD', 'RED_CARD')),
    minute         INT CHECK (minute IS NULL OR minute BETWEEN 0 AND 130)
);
CREATE INDEX ix_match_events_match ON match_events (match_id);

CREATE TABLE lineups (
    id         BIGSERIAL PRIMARY KEY,
    match_id   BIGINT NOT NULL REFERENCES matches (id) ON DELETE CASCADE,
    team_id    BIGINT NOT NULL REFERENCES teams (id),
    formation  VARCHAR(10) NOT NULL DEFAULT 'F_2_3_1'
        CHECK (formation IN ('F_3_2_1', 'F_2_3_1', 'F_4_1_1', 'F_1_3_2')),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_lineups_match_team UNIQUE (match_id, team_id)
);

CREATE TABLE lineup_players (
    lineup_id      BIGINT NOT NULL REFERENCES lineups (id) ON DELETE CASCADE,
    player_user_id BIGINT NOT NULL REFERENCES users (id),
    starter        BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (lineup_id, player_user_id)
);
