-- account_roles
DELETE FROM account_role WHERE account_role_id = 10;
ALTER TABLE account_role DROP CONSTRAINT account_role_pkey;
ALTER TABLE account_role DROP CONSTRAINT account_role_account_role_id_fkey;
ALTER TABLE account_role ALTER COLUMN account_role_id DROP NOT NUll;
UPDATE account_role SET account_role_id = NULL;
ALTER TABLE account_role RENAME COLUMN account_role_id TO role;
ALTER TABLE account_role ALTER COLUMN role TYPE VARCHAR(32);
UPDATE account_role SET role = 'ADMIN';
ALTER TABLE account_role ALTER COLUMN role SET NOT NUll;
ALTER TABLE account_role ADD PRIMARY KEY (account_id, role);
DROP TABLE account_roles;

-- address_type
ALTER TABLE address ADD COLUMN address_type VARCHAR(64);

UPDATE address SET address_type = 'HOME' WHERE address_type_id = 1;
UPDATE address SET address_type = 'WORK' WHERE address_type_id = 2;
UPDATE address SET address_type = 'SCHOOL' WHERE address_type_id = 3;
UPDATE address SET address_type = 'OTHER' WHERE address_type_id = 4;

ALTER TABLE address ALTER COLUMN address_type SET NOT NULL;
ALTER TABLE address DROP COLUMN address_type_id;

ALTER TABLE address ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE address set created2 = created;
ALTER TABLE address DROP COLUMN created;
ALTER TABLE address RENAME COLUMN created2 to created;

ALTER TABLE address ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE address set modified2 = modified;
ALTER TABLE address DROP COLUMN modified;
ALTER TABLE address RENAME COLUMN modified2 to modified;

DROP TABLE address_type;

-- attribute/attribute_type

ALTER TABLE player_attribute DROP CONSTRAINT player_attribute_pkey;
ALTER TABLE player_attribute DROP COLUMN attribute_id;
ALTER TABLE player_attribute ADD COLUMN attribute_type VARCHAR(32);
UPDATE player_attribute SET attribute_type = 'LAST_WISH';
ALTER TABLE player_attribute ALTER COLUMN attribute_type SET NOT NULL;
ALTER TABLE player_attribute ADD PRIMARY KEY (player_id, attribute_type);

ALTER TABLE player_attribute ADD COLUMN value2 TEXT;
UPDATE player_attribute set value2 = value;
ALTER TABLE player_attribute DROP COLUMN value;
ALTER TABLE player_attribute RENAME COLUMN value2 to value;
ALTER TABLE player_attribute ALTER COLUMN value SET NOT NULL;

ALTER TABLE player_attribute ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE player_attribute set created2 = created;
ALTER TABLE player_attribute DROP COLUMN created;
ALTER TABLE player_attribute RENAME COLUMN created2 to created;

ALTER TABLE player_attribute ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE player_attribute set modified2 = modified;
ALTER TABLE player_attribute DROP COLUMN modified;
ALTER TABLE player_attribute RENAME COLUMN modified2 to modified;

DELETE FROM player_attribute WHERE value IS NULL OR trim(both from value) = '' OR value = '.' ;

ALTER TABLE game_attribute DROP CONSTRAINT game_attribute_pkey;
ALTER TABLE game_attribute DROP COLUMN attribute_id;
ALTER TABLE game_attribute ADD COLUMN attribute_type VARCHAR(32);
UPDATE game_attribute SET attribute_type = 'ORDNANCE_TYPE';
ALTER TABLE game_attribute ALTER COLUMN attribute_type SET NOT NULL;
ALTER TABLE game_attribute ADD PRIMARY KEY (game_id, attribute_type);

ALTER TABLE game_attribute DROP COLUMN value;
ALTER TABLE game_attribute ADD COLUMN value TEXT;
UPDATE game_attribute SET value = 'WATER_WEAPONS';
ALTER TABLE game_attribute ALTER COLUMN value SET NOT NULL;

ALTER TABLE game_attribute ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE game_attribute set created2 = created;
ALTER TABLE game_attribute DROP COLUMN created;
ALTER TABLE game_attribute RENAME COLUMN created2 to created;

ALTER TABLE game_attribute ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE game_attribute set modified2 = modified;
ALTER TABLE game_attribute DROP COLUMN modified;
ALTER TABLE game_attribute RENAME COLUMN modified2 to modified;

ALTER TABLE team_attribute DROP CONSTRAINT team_attribute_pkey;
ALTER TABLE team_attribute DROP COLUMN attribute_id;
ALTER TABLE team_attribute ADD COLUMN attribute_type VARCHAR(32) NOT NULL;
ALTER TABLE team_attribute ADD PRIMARY KEY (team_id, attribute_type);

ALTER TABLE team_attribute DROP COLUMN value;
ALTER TABLE team_attribute ADD COLUMN value TEXT NOT NULL;
ALTER TABLE team_attribute ALTER COLUMN value SET NOT NULL;

ALTER TABLE team_attribute ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE team_attribute set created2 = created;
ALTER TABLE team_attribute DROP COLUMN created;
ALTER TABLE team_attribute RENAME COLUMN created2 to created;

ALTER TABLE team_attribute ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE team_attribute set modified2 = modified;
ALTER TABLE team_attribute DROP COLUMN modified;
ALTER TABLE team_attribute RENAME COLUMN modified2 to modified;

DROP TABLE attribute;
DROP TABLE game_type_required_attribute_type;
DROP TABLE attribute_type;

-- dispute_type
ALTER TABLE dispute ADD COLUMN dispute_type VARCHAR(64);

UPDATE dispute SET dispute_type = 'BAD_PHOTO' WHERE dispute_type_id = 1;
UPDATE dispute SET dispute_type = 'BAD_ACCOUNT_INFO' WHERE dispute_type_id = 2;
UPDATE dispute SET dispute_type = 'BREAKING_GAME_RULES' WHERE dispute_type_id = 3;
UPDATE dispute SET dispute_type = 'UNSPORTSMANLIKE_CONDUCT' WHERE dispute_type_id = 4;
UPDATE dispute SET dispute_type = 'KILL_DISAGREEMENT' WHERE dispute_type_id = 5;
UPDATE dispute SET dispute_type = 'OTHER' WHERE dispute_type_id = 6;

ALTER TABLE dispute ALTER COLUMN dispute_type SET NOT NULL;
ALTER TABLE dispute DROP COLUMN dispute_type_id;

DROP TABLE dispute_type;

-- dispute_status
ALTER TABLE dispute ADD COLUMN dispute_status VARCHAR(64);

UPDATE dispute SET dispute_status = 'OPEN' WHERE dispute_status_id = 1;
UPDATE dispute SET dispute_status = 'CLOSED' WHERE dispute_status_id = 2;

ALTER TABLE dispute ALTER COLUMN dispute_status SET NOT NULL;
ALTER TABLE dispute DROP COLUMN dispute_status_id;

DROP TABLE dispute_status;

ALTER TABLE dispute ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE dispute set created2 = created;
ALTER TABLE dispute DROP COLUMN created;
ALTER TABLE dispute RENAME COLUMN created2 to created;

ALTER TABLE dispute ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE dispute set modified2 = modified;
ALTER TABLE dispute DROP COLUMN modified;
ALTER TABLE dispute RENAME COLUMN modified2 to modified;

-- event_type
ALTER TABLE event ADD COLUMN event_type VARCHAR(64);

UPDATE event SET event_type = 'ASSIGN' WHERE event_type_id = 1;
UPDATE event SET event_type = 'CAMPER_ELIMINATION' WHERE event_type_id = 2;

ALTER TABLE event ALTER COLUMN event_type SET NOT NULL;
ALTER TABLE event DROP COLUMN event_type_id;

ALTER TABLE event ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE event set created2 = created;
ALTER TABLE event DROP COLUMN created;
ALTER TABLE event RENAME COLUMN created2 to created;

ALTER TABLE event ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE event set modified2 = modified;
ALTER TABLE event DROP COLUMN modified;
ALTER TABLE event RENAME COLUMN modified2 to modified;

DROP TABLE event_type;

-- game_type
ALTER TABLE game ADD COLUMN game_type VARCHAR(64);
UPDATE game SET game_type = 'ASSASSIN';
ALTER TABLE game ALTER COLUMN game_type SET NOT NULL;
ALTER TABLE game DROP COLUMN game_type_id;

ALTER TABLE game ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE game set created2 = created;
ALTER TABLE game DROP COLUMN created;
ALTER TABLE game RENAME COLUMN created2 to created;

ALTER TABLE game ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE game set modified2 = modified;
ALTER TABLE game DROP COLUMN modified;
ALTER TABLE game RENAME COLUMN modified2 to modified;

DROP TABLE game_type;

-- game_roles
ALTER TABLE player ADD COLUMN game_role VARCHAR(64);

UPDATE player SET game_role = 'PLAYER' WHERE game_role_id = 20;
UPDATE player SET game_role = 'PLAYER' WHERE game_role_id = 21;
UPDATE player SET game_role = 'GAME_MASTER' WHERE game_role_id = 22;

ALTER TABLE player ALTER COLUMN game_role SET NOT NULL;
ALTER TABLE player DROP COLUMN game_role_id;
DROP TABLE game_roles;

-- history_type
ALTER TABLE history ADD COLUMN history_type VARCHAR(64);

UPDATE history SET history_type = 'KILL' WHERE history_type_id = 1;
UPDATE history SET history_type = 'DEFENSIVE_KILL' WHERE history_type_id = 2;
UPDATE history SET history_type = 'ADMIN_ELIMINATION' WHERE history_type_id = 3 ;
UPDATE history SET history_type = 'MISSED_CHECKPOINT' WHERE history_type_id = 4 OR obituary LIKE '%Yikes, % did not make it to the checkpoint in time%';
UPDATE history SET history_type = 'ZERO_KILLS' WHERE history_type_id = 5;

ALTER TABLE history ALTER COLUMN history_type SET NOT NULL;
ALTER TABLE history DROP COLUMN history_type_id;

ALTER TABLE history ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE history set created2 = created;
ALTER TABLE history DROP COLUMN created;
ALTER TABLE history RENAME COLUMN created2 to created;

ALTER TABLE history ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE history set modified2 = modified;
ALTER TABLE history DROP COLUMN modified;
ALTER TABLE history RENAME COLUMN modified2 to modified;

DROP TABLE history_type;

-- image_type
ALTER TABLE image ADD COLUMN image_type VARCHAR(64);

UPDATE image SET image_type = 'GAME_BADGE' WHERE image_type_id = 1;
UPDATE image SET image_type = 'PHOTO_ID' WHERE image_type_id = 2;
UPDATE image SET image_type = 'AVATAR' WHERE image_type_id = 3;
UPDATE image SET image_type = 'ACTION_PHOTO' WHERE image_type_id = 4;
UPDATE image SET image_type = 'ACCOLADE' WHERE image_type_id = 5;
UPDATE image SET image_type = 'POST_ATTACHMENT' WHERE image_type_id = 6;
UPDATE image SET image_type = 'THUMBNAIL' WHERE image_type_id = 7;
UPDATE image SET image_type = 'ICON' WHERE image_type_id = 8;
UPDATE image SET image_type = 'TEAM_BADGE' WHERE image_type_id = 9;
UPDATE image SET image_type = 'SPONSOR_IMAGE' WHERE image_type_id = 10;

ALTER TABLE image ALTER COLUMN image_type SET NOT NULL;
ALTER TABLE image DROP COLUMN image_type_id;

ALTER TABLE image ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE image set created2 = created;
ALTER TABLE image DROP COLUMN created;
ALTER TABLE image RENAME COLUMN created2 to created;

ALTER TABLE image ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE image set modified2 = modified;
ALTER TABLE image DROP COLUMN modified;
ALTER TABLE image RENAME COLUMN modified2 to modified;

DROP TABLE image_type;

-- player_roles
ALTER TABLE player ADD COLUMN player_role VARCHAR(64);

UPDATE player SET player_role = 'PLAYER' WHERE player_role_id = 30;
UPDATE player SET player_role = 'VIP' WHERE player_role_id = 31;

ALTER TABLE player ALTER COLUMN player_role SET NOT NULL;
ALTER TABLE player DROP COLUMN player_role_id;

ALTER TABLE player ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE player set created2 = created;
ALTER TABLE player DROP COLUMN created;
ALTER TABLE player RENAME COLUMN created2 to created;

ALTER TABLE player ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE player set modified2 = modified;
ALTER TABLE player DROP COLUMN modified;
ALTER TABLE player RENAME COLUMN modified2 to modified;

DROP TABLE player_roles;

-- post_type
ALTER TABLE post ADD COLUMN post_type VARCHAR(64);

UPDATE post SET post_type = 'TEXT' WHERE post_type_id = 2;
UPDATE post SET post_type = 'GAME_EVENT' WHERE post_type_id = 6;
UPDATE post SET post_type = 'KILL_EVENT' WHERE post_type_id = 7;

ALTER TABLE post ALTER COLUMN post_type SET NOT NULL;
ALTER TABLE post DROP COLUMN post_type_id;

ALTER TABLE post ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE post set created2 = created;
ALTER TABLE post DROP COLUMN created;
ALTER TABLE post RENAME COLUMN created2 to created;

ALTER TABLE post ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE post set modified2 = modified;
ALTER TABLE post DROP COLUMN modified;
ALTER TABLE post RENAME COLUMN modified2 to modified;

DROP TABLE post_type;

-- rule_type
ALTER TABLE rule ADD COLUMN rule_type VARCHAR(64);

UPDATE rule SET rule_type = 'ALL_GAMES' WHERE rule_type_id = 1;
UPDATE rule SET rule_type = 'LOCATION_RESTRICTION' WHERE rule_type_id = 2;
UPDATE rule SET rule_type = 'SAFE_ZONES' WHERE rule_type_id = 3;
UPDATE rule SET rule_type = 'WATER_WEAPONS' WHERE rule_type_id = 4;
UPDATE rule SET rule_type = 'ROUND_ROBIN' WHERE rule_type_id = 5;
UPDATE rule SET rule_type = 'ASSASSIN' WHERE rule_type_id = 6;
UPDATE rule SET rule_type = 'NERF_WEAPONS' WHERE rule_type_id = 7;
UPDATE rule SET rule_type = 'SPOONS_AND_SOCKS' WHERE rule_type_id = 8;

ALTER TABLE rule ALTER COLUMN rule_type SET NOT NULL;
ALTER TABLE rule DROP COLUMN rule_type_id;

ALTER TABLE rule ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE rule set created2 = created;
ALTER TABLE rule DROP COLUMN created;
ALTER TABLE rule RENAME COLUMN created2 to created;

ALTER TABLE rule ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE rule set modified2 = modified;
ALTER TABLE rule DROP COLUMN modified;
ALTER TABLE rule RENAME COLUMN modified2 to modified;

DROP TABLE rule_type;

-- token_type
ALTER TABLE account_token RENAME TO token;
ALTER TABLE token ADD COLUMN token_type VARCHAR(64);

DELETE FROM token;

ALTER TABLE token ALTER COLUMN token_type SET NOT NULL;
ALTER TABLE token DROP COLUMN token_type_id;

ALTER TABLE token ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE token set created2 = created;
ALTER TABLE token DROP COLUMN created;
ALTER TABLE token RENAME COLUMN created2 to created;

DROP TABLE token_type;

-- transaction_type
ALTER TABLE transaction ADD COLUMN transaction_type VARCHAR(64);

UPDATE transaction SET transaction_type = 'PAYMENT' WHERE transaction_type_id = 1;
UPDATE transaction SET transaction_type = 'REFUND' WHERE transaction_type_id = 2;
UPDATE transaction SET transaction_type = 'FAILED' WHERE transaction_type_id = 3;

ALTER TABLE transaction ALTER COLUMN transaction_type SET NOT NULL;
ALTER TABLE transaction DROP COLUMN transaction_type_id;

ALTER TABLE transaction ADD COLUMN created2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE transaction set created2 = created;
ALTER TABLE transaction DROP COLUMN created;
ALTER TABLE transaction RENAME COLUMN created2 to created;

ALTER TABLE transaction ADD COLUMN modified2 TIMESTAMP NOT NULL DEFAULT NOW();
UPDATE transaction set modified2 = modified;
ALTER TABLE transaction DROP COLUMN modified;
ALTER TABLE transaction RENAME COLUMN modified2 to modified;

DROP TABLE transaction_type;