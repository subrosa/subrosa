-- create enrollment_field for last wishes and the non-avatar player images
--
ALTER TABLE player_attribute ALTER value DROP NOT NULL;

ALTER TABLE enrollment_field ADD COLUMN tmp_field INTEGER;

INSERT INTO enrollment_field ( game_id, field_id, name, description, type, index )
  SELECT g.game_id,
    substring(md5(random()::text), 0, 8),
    'Last Wish',
    'A request you wish to be honored in your last moments',
    'TEXT',
    0
  FROM game g
;

UPDATE player_attribute SET name = ef.field_id
  FROM enrollment_field ef
      JOIN game g ON g.game_id = ef.game_id
      JOIN team t ON t.game_id = g.game_id
      JOIN player p ON p.team_id = t.team_id
  WHERE p.player_id = player_attribute.player_id
;

INSERT INTO enrollment_field ( game_id, field_id, name, description, type, index, tmp_field )
  SELECT g.game_id,
    substring(md5(random()::text), 0, 8),
    'Action Photo',
    'Image showing you in action',
    'IMAGE',
    1,
    1
  FROM game g
;

INSERT INTO player_attribute ( player_id, name, value_ref_id )
    SELECT pi.player_id, ef.field_id, pi.image_id
    FROM player_image pi
      JOIN player p ON p.player_id = pi.player_id
      JOIN team t ON t.team_id = p.team_id
      JOIN game g ON g.game_id = t.game_id
      JOIN enrollment_field ef ON ef.game_id = g.game_id AND ef.tmp_field = 1
    WHERE pi.image_type = 'ACTION_PHOTO'
;

DELETE FROM player_image WHERE image_type = 'ACTION_PHOTO';

INSERT INTO enrollment_field ( game_id, field_id, name, description, type, index, tmp_field )
  SELECT g.game_id,
    substring(md5(random()::text), 0, 8),
    'Photo ID',
    'Standard photo ID',
    'IMAGE',
    2,
    2
  FROM game g
;

INSERT INTO player_attribute ( player_id, name, value_ref_id )
  SELECT pi.player_id, ef.field_id, pi.image_id
  FROM player_image pi
    JOIN player p ON p.player_id = pi.player_id
    JOIN team t ON t.team_id = p.team_id
    JOIN game g ON g.game_id = t.game_id
    JOIN enrollment_field ef ON ef.game_id = g.game_id AND ef.tmp_field = 2
  WHERE pi.image_type = 'PHOTO_ID'
;

DELETE FROM player_image WHERE image_type = 'PHOTO_ID';

ALTER TABLE enrollment_field DROP COLUMN tmp_field;

--
-- done
