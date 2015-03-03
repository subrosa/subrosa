ALTER TABLE lifecycle_event ADD game_id INTEGER REFERENCES game(game_id);
ALTER TABLE lifecycle_event ADD created TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE lifecycle_event ADD modified TIMESTAMP NOT NULL DEFAULT NOW();

UPDATE lifecycle_event
SET game_id = l.game_id
FROM lifecycle_event le
  JOIN lifecycle l USING (lifecycle_id);

ALTER TABLE lifecycle_event ALTER game_id SET NOT NULL;
ALTER TABLE lifecycle_event DROP COLUMN lifecycle_id;
DROP TABLE lifecycle;

ALTER TABLE lifecycle_event ADD COLUMN event_date TIMESTAMP;
ALTER TABLE lifecycle_event ADD COLUMN trigger_event_id INTEGER REFERENCES lifecycle_event(event_id);
ALTER TABLE lifecycle_event ADD COLUMN trigger_type TEXT;

INSERT INTO lifecycle_event (game_id, event_type, event_class, event_date)
  SELECT game_id, 'SCHEDULED', 'registrationStart', registration_start FROM game;
INSERT INTO lifecycle_event (game_id, event_type, event_class, event_date)
  SELECT game_id, 'SCHEDULED', 'registrationEnd', registration_end FROM game;
INSERT INTO lifecycle_event (game_id, event_type, event_class, event_date)
  SELECT game_id, 'SCHEDULED', 'gameStart', game_start FROM game;
INSERT INTO lifecycle_event (game_id, event_type, event_class, event_date)
  SELECT game_id, 'SCHEDULED', 'gameEnd', game_end FROM game;

ALTER TABLE game DROP COLUMN registration_start;
ALTER TABLE game DROP COLUMN registration_end;
ALTER TABLE game DROP COLUMN game_start;
ALTER TABLE game DROP COLUMN game_end;

