ALTER TABLE game ADD COLUMN owner_id INTEGER REFERENCES account(account_id);

UPDATE game g SET owner_id = p.account_id
FROM player p
  JOIN team t USING (team_id)
  WHERE g.game_id = t.game_id AND game_role = 'GAME_MASTER';

ALTER TABLE game ALTER owner_id SET NOT NULL;