ALTER TABLE game ADD COLUMN game_start TIMESTAMP WITHOUT TIME ZONE;
UPDATE game SET game_start = l.game_start
 FROM lifecycle l WHERE game.game_id = l.game_id;
ALTER TABLE game ADD COLUMN game_end TIMESTAMP WITHOUT TIME ZONE;
UPDATE game SET game_end = l.game_end
 FROM lifecycle l WHERE game.game_id = l.game_id;
ALTER TABLE game ADD COLUMN registration_start TIMESTAMP WITHOUT TIME ZONE;
UPDATE game SET registration_start = l.registration_start
 FROM lifecycle l WHERE game.game_id = l.game_id;
ALTER TABLE game ADD COLUMN registration_end TIMESTAMP WITHOUT TIME ZONE;
UPDATE game SET registration_end = l.registration_end
 FROM lifecycle l WHERE game.game_id = l.game_id;

ALTER TABLE lifecycle DROP COLUMN game_start;
ALTER TABLE lifecycle DROP COLUMN game_end;
ALTER TABLE lifecycle DROP COLUMN registration_start;
ALTER TABLE lifecycle DROP COLUMN registration_end;

