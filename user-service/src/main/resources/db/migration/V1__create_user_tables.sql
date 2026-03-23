-- Users Table
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       keycloak_id VARCHAR(255) NOT NULL UNIQUE,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       avatar_url VARCHAR(500),
                       rating INTEGER NOT NULL DEFAULT 1000,
                       wins INTEGER NOT NULL DEFAULT 0,
                       losses INTEGER NOT NULL DEFAULT 0,
                       rank_title VARCHAR(50) DEFAULT 'Novice',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User Statistics Table
CREATE TABLE user_statistics (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                 total_battles INTEGER NOT NULL DEFAULT 0,
                                 problems_solved INTEGER NOT NULL DEFAULT 0,
                                 easy_solved INTEGER NOT NULL DEFAULT 0,
                                 medium_solved INTEGER NOT NULL DEFAULT 0,
                                 hard_solved INTEGER NOT NULL DEFAULT 0,
                                 average_solve_time_ms BIGINT NOT NULL DEFAULT 0,
                                 preferred_language VARCHAR(20),
                                 current_streak INTEGER NOT NULL DEFAULT 0,
                                 max_streak INTEGER NOT NULL DEFAULT 0,
                                 hints_used INTEGER NOT NULL DEFAULT 0,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_users_rating ON users(rating DESC);
CREATE INDEX idx_users_wins ON users(wins DESC);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_keycloak_id ON users(keycloak_id);
CREATE INDEX idx_user_statistics_user_id ON user_statistics(user_id);

-- Function to update timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_statistics_updated_at
    BEFORE UPDATE ON user_statistics
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();