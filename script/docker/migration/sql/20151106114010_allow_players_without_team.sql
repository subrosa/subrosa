ALTER TABLE player ALTER COLUMN team_id DROP NOT NULL;
ALTER TABLE player ADD COLUMN game_id INTEGER REFERENCES game(game_id);

UPDATE player SET game_id = t.game_id
FROM team t
WHERE player.team_id = t.team_id;

ALTER TABLE player ALTER COLUMN game_id SET NOT NULL;

CREATE INDEX ON player(team_id);
CREATE INDEX ON player(game_id);
CREATE INDEX ON team(game_id);

