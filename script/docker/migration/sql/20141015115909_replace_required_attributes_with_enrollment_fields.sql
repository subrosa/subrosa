DROP TABLE required_attribute;

CREATE TABLE enrollment_field (
  game_id INTEGER NOT NULL REFERENCES game(game_id),
  field_id VARCHAR(7) NOT NULL,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  type VARCHAR(32) NOT NULL,
  index INTEGER NOT NULL,
  PRIMARY KEY (game_id, field_id)
);

