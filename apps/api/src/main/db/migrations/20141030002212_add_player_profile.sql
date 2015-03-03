CREATE TABLE player_profile (
  player_profile_id SERIAL PRIMARY KEY,
  account_id        INTEGER      NOT NULL REFERENCES account (account_id),
  name              VARCHAR(128) NOT NULL,
  image_id          INTEGER      NOT NULL REFERENCES image (image_id)
);

CREATE INDEX player_profile_account_id_idx ON player_profile (account_id);
CREATE INDEX player_profile_image_id_idx ON player_profile (image_id);

