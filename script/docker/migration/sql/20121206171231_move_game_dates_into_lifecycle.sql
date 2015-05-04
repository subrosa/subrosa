ALTER TABLE lifecycle ADD COLUMN game_id INTEGER UNIQUE REFERENCES game(game_id);

INSERT INTO lifecycle ( registration_start, registration_end, game_start, game_end, game_id )
    SELECT registration_end_time - INTERVAL'1 month',
      registration_end_time,
      registration_end_time,
      end_time,
      game_id
    FROM game;

INSERT INTO game_lifecycle (game_id, lifecycle_id)
    SELECT game_id, lifecycle_id FROM lifecycle;

ALTER TABLE lifecycle DROP COLUMN game_id;

ALTER TABLE game DROP COLUMN registration_end_time;
ALTER TABLE game DROP COLUMN end_time;

