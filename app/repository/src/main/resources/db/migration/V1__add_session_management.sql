-- Add session management columns to player_context table
ALTER TABLE player_context
    ADD COLUMN session_id VARCHAR(36) NOT NULL DEFAULT gen_random_uuid(),
    ADD COLUMN start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;

-- Add indexes for session management
CREATE INDEX idx_session_id ON player_context(session_id);
CREATE INDEX idx_player_id ON player_context(player_id);

-- Add unique constraint on session_id
ALTER TABLE player_context
    ADD CONSTRAINT uk_session_id UNIQUE (session_id);

-- Update existing records to have unique session IDs
UPDATE player_context
SET session_id = gen_random_uuid()
WHERE session_id IS NULL; 