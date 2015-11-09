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

