-- Battle Database Initialization
-- This script runs when the container first starts

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE battle_db TO arena_user;

-- Log initialization
DO $$
BEGIN
    RAISE NOTICE 'Battle database initialized successfully!';
END $$;