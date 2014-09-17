ALTER TABLE game DROP CONSTRAINT name;
CREATE INDEX game_name ON game(name);

ALTER TABLE game ADD COLUMN url TEXT;
UPDATE game SET url = lower(regexp_replace(regexp_replace(name, '[^-a-zA-Z0-9 ]', '', 'g'), '[\s-]+', '-', 'g'));
ALTER TABLE game ALTER COLUMN url SET NOT NULL;
UPDATE game SET url = 'fcj-year-12-2' WHERE game_id = 169;
UPDATE game SET url = 'pittsburgh-capa-seniors-2' WHERE game_id = 75;
UPDATE game SET url = 'test-game-2' WHERE game_id = 112;
UPDATE game SET url = 'test-2' WHERE game_id = 57;
CREATE UNIQUE INDEX game_url ON game(url);
