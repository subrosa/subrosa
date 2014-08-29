CREATE TABLE game_lifecycle (
  game_id INTEGER PRIMARY KEY REFERENCES game(game_id),
  event_id INTEGER NOT NULL REFERENCES lifecycle_event(event_id)
);

INSERT INTO game_lifecycle (game_id, event_id)
    SELECT l.game_id, le.event_id
      FROM lifecycle l
      INNER JOIN lifecycle_event le ON (le.lifecycle_id = l.lifecycle_id)
;

DROP TABLE lifecycle;

ALTER TABLE lifecycle_event DROP COLUMN lifecycle_id;
