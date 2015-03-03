CREATE TABLE game_location (
  game_id INTEGER NOT NULL REFERENCES game(game_id),
  location_id INTEGER NOT NULL REFERENCES location(location_id)
);

CREATE INDEX game_location_game_id_idx ON game_location(game_id);

