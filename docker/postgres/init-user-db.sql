-- User Database Initialization
-- This script runs when the container first starts

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE user_db TO arena_user;

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'User database initialized successfully!';
END $$;