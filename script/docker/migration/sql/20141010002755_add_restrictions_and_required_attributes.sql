CREATE TABLE restriction (
  restriction_id SERIAL PRIMARY KEY,
  game_id INTEGER NOT NULL REFERENCES game(game_id),
  type VARCHAR(32) NOT NULL,
  value TEXT,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  modified TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX ON restriction(game_id);

CREATE TABLE required_attribute (
  required_attribute_id SERIAL PRIMARY KEY,
  game_id INTEGER NOT NULL REFERENCES game(game_id),
  name TEXT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  modified TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
CREATE INDEX ON required_attribute(game_id);

ALTER TABLE player_attribute RENAME attribute_type TO name;
