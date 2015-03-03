-- shift player.name and player_image info into a player_profile and reference it from player
--

ALTER TABLE player ADD COLUMN player_profile_id INTEGER REFERENCES player_profile(player_profile_id);

ALTER TABLE player_profile ADD COLUMN tmp_player_id INTEGER REFERENCES player(player_id);

ALTER TABLE player_profile ALTER image_id DROP NOT NULL;

INSERT INTO player_profile ( account_id, name, image_id, tmp_player_id )
  SELECT p.account_id, p.name, pi.image_id, p.player_id
  FROM player p
    LEFT JOIN player_image pi ON pi.player_id = p.player_id AND pi.image_type = 'AVATAR'
;

UPDATE player SET player_profile_id = pp.player_profile_id
  FROM player_profile pp
  WHERE pp.tmp_player_id = player_id
;

ALTER TABLE player ALTER player_profile_id SET NOT NULL;

ALTER TABLE player DROP COLUMN name;

ALTER TABLE player_profile DROP COLUMN tmp_player_id;
DROP TABLE player_image;

--
-- done
