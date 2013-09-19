ALTER TABLE lifecycle ADD game_id INTEGER REFERENCES game(game_id);

UPDATE lifecycle l SET game_id = gl.game_id
  FROM game_lifecycle gl
  WHERE gl.lifecycle_id = l.lifecycle_id;

DROP TABLE game_lifecycle;

