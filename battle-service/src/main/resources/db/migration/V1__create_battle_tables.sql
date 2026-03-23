-- Problems Table
CREATE TABLE problems (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          title VARCHAR(200) NOT NULL,
                          description TEXT NOT NULL,
                          difficulty VARCHAR(20) NOT NULL,
                          time_limit_seconds INTEGER NOT NULL DEFAULT 5,
                          memory_limit_mb INTEGER NOT NULL DEFAULT 256,
                          input_format TEXT,
                          output_format TEXT,
                          constraints TEXT,
                          starter_code_java TEXT,
                          starter_code_python TEXT,
                          starter_code_javascript TEXT,
                          created_by UUID,
                          is_active BOOLEAN NOT NULL DEFAULT true,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Test Cases Table
CREATE TABLE test_cases (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            problem_id UUID NOT NULL REFERENCES problems(id) ON DELETE CASCADE,
                            input TEXT NOT NULL,
                            expected_output TEXT NOT NULL,
                            is_hidden BOOLEAN NOT NULL DEFAULT false,
                            order_index INTEGER NOT NULL DEFAULT 0,
                            explanation TEXT
);

-- Battle Rooms Table
CREATE TABLE battle_rooms (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              room_code VARCHAR(10) NOT NULL UNIQUE,
                              problem_id UUID REFERENCES problems(id),
                              status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
                              max_players INTEGER NOT NULL DEFAULT 2,
                              winner_id UUID,
                              is_ranked BOOLEAN NOT NULL DEFAULT true,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              started_at TIMESTAMP,
                              ended_at TIMESTAMP
);

-- Room Participants Table
CREATE TABLE room_participants (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   room_id UUID NOT NULL REFERENCES battle_rooms(id) ON DELETE CASCADE,
                                   user_id UUID NOT NULL,
                                   username VARCHAR(50) NOT NULL,
                                   rating INTEGER NOT NULL DEFAULT 1000,
                                   is_ready BOOLEAN NOT NULL DEFAULT false,
                                   has_submitted BOOLEAN NOT NULL DEFAULT false,
                                   test_cases_passed INTEGER DEFAULT 0,
                                   submission_time_ms BIGINT,
                                   hints_used INTEGER NOT NULL DEFAULT 0,
                                   joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT unique_room_user UNIQUE (room_id, user_id)
);

-- Submissions Table
CREATE TABLE submissions (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             room_id UUID NOT NULL REFERENCES battle_rooms(id) ON DELETE CASCADE,
                             user_id UUID NOT NULL,
                             code TEXT NOT NULL,
                             language VARCHAR(20) NOT NULL,
                             status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                             execution_time_ms INTEGER,
                             memory_used_kb INTEGER,
                             test_cases_passed INTEGER DEFAULT 0,
                             total_test_cases INTEGER DEFAULT 0,
                             error_message TEXT,
                             submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_is_active ON problems(is_active);
CREATE INDEX idx_test_cases_problem_id ON test_cases(problem_id);
CREATE INDEX idx_battle_rooms_status ON battle_rooms(status);
CREATE INDEX idx_battle_rooms_room_code ON battle_rooms(room_code);
CREATE INDEX idx_room_participants_room_id ON room_participants(room_id);
CREATE INDEX idx_room_participants_user_id ON room_participants(user_id);
CREATE INDEX idx_submissions_room_id ON submissions(room_id);
CREATE INDEX idx_submissions_user_id ON submissions(user_id);
CREATE INDEX idx_submissions_status ON submissions(status);

-- Function to update timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers
CREATE TRIGGER update_problems_updated_at
    BEFORE UPDATE ON problems
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();