ALTER TABLE game ADD COLUMN game_type TEXT NOT NULL DEFAULT '';
UPDATE game SET game_type = 'ASSASSIN';
ALTER TABLE game DROP COLUMN game_type_id;

-- TODO take care of foreign key constraints
--DROP TABLE game_type;
