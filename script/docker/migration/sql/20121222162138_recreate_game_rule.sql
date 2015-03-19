CREATE TABLE game_rule (
  game_id       INTEGER NOT NULL REFERENCES game(game_id),
  rule_id       INTEGER NOT NULL REFERENCES rule(rule_id)
);
