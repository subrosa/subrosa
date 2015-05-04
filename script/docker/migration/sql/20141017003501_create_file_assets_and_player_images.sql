CREATE TABLE file_asset (
  file_asset_id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  size BIGINT,
  mime_type VARCHAR(255),
  storage_mechanism VARCHAR(32) NOT NULL DEFAULT 'LOCAL',
  uri VARCHAR(255),
  image_id INTEGER -- temporary
);

INSERT INTO file_asset (name, uri, image_id)
    SELECT regexp_replace(uri, '^.+[/]', '') AS name, uri, image_id FROM image;

ALTER TABLE image ADD COLUMN file_asset_id INTEGER REFERENCES file_asset(file_asset_id);
UPDATE image SET file_asset_id = fa.file_asset_id
  FROM file_asset fa
  WHERE image.image_id = fa.image_id
;

ALTER TABLE file_asset DROP COLUMN image_id;
ALTER TABLE image ALTER COLUMN file_asset_id SET NOT NULL;

CREATE TABLE player_image (
  player_id INTEGER NOT NULL REFERENCES player(player_id),
  image_id INTEGER NOT NULL REFERENCES image(image_id),
  image_type VARCHAR(32) NOT NULL
);
CREATE INDEX ON player_image(player_id);
CREATE INDEX ON player_image(image_id);

INSERT INTO player_image (player_id, image_id, image_type)
    SELECT p.player_id, ai.image_id, i.image_type
    FROM account_image ai
      INNER JOIN image i USING (image_id)
      INNER JOIN player p USING (account_id)
    WHERE (image_id NOT BETWEEN 1 AND 12 AND image_id NOT BETWEEN 271 AND 280);

ALTER TABLE image ADD COLUMN account_id INTEGER REFERENCES account(account_id);
ALTER TABLE image ADD COLUMN index INTEGER;
CREATE UNIQUE INDEX ON image(account_id, index);

UPDATE image SET account_id = ai.account_id
  FROM account_image ai
  WHERE image.image_id = ai.image_id
    AND (image.image_id NOT BETWEEN 1 AND 12 AND image.image_id NOT BETWEEN 271 AND 280);

DROP TABLE account_image;

ALTER TABLE image DROP COLUMN uri;
ALTER TABLE image DROP COLUMN image_type;

UPDATE image SET account_id = g.owner_id
  FROM game g
  WHERE g.image_id = image.image_id;

-- ALTER TABLE image ALTER COLUMN account_id SET NOT NULL;
UPDATE image SET index = x.index
FROM (
  SELECT
    image_id,
    ROW_NUMBER()
    OVER (PARTITION BY account_id
      ORDER BY image_Id) - 1 AS index
  FROM image
) x
WHERE image.image_id = x.image_id;
ALTER TABLE image ALTER COLUMN index SET NOT NULL;
