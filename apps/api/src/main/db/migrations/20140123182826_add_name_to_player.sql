ALTER TABLE player ADD COLUMN name VARCHAR(128);

UPDATE player SET name = a.username
FROM account a
WHERE player.account_id = a.account_id;

UPDATE player SET name = '' WHERE name IS NULL;

ALTER TABLE player ALTER name SET NOT NULL;