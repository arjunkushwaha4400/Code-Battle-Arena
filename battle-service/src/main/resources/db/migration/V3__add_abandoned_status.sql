ALTER TABLE battle_rooms
DROP CONSTRAINT IF EXISTS battle_rooms_status_check;

ALTER TABLE battle_rooms
    ADD CONSTRAINT battle_rooms_status_check
        CHECK (status IN ('WAITING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'ABANDONED'));