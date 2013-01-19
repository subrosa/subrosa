ALTER TABLE game DROP CONSTRAINT name;
CREATE INDEX game_name ON game(name);

ALTER TABLE game ADD COLUMN url TEXT;
UPDATE game SET url = lower(regexp_replace(regexp_replace(name, '[^-a-zA-Z0-9 ]', '', 'g'), '[\s-]+', '-', 'g'));
ALTER TABLE game ALTER COLUMN url SET NOT NULL;
CREATE UNIQUE INDEX game_url ON game(url);
