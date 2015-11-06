-- add index to account_address
ALTER TABLE account_address ADD COLUMN index INTEGER;
CREATE UNIQUE INDEX ON account_address(account_id, index);

UPDATE account_address SET index = x.index
FROM (
       SELECT
         address_id,
         ROW_NUMBER()
         OVER (PARTITION BY account_id
           ORDER BY address_id) - 1 AS index
       FROM account_address
     ) x
WHERE account_address.address_id = x.address_id;
ALTER TABLE account_address ALTER COLUMN index SET NOT NULL;


-- add index to player_profile
ALTER TABLE player_profile ADD COLUMN index INTEGER;
CREATE UNIQUE INDEX ON player_profile(account_id, index);

UPDATE player_profile SET index = x.index
FROM (
       SELECT
         player_profile_id,
         ROW_NUMBER()
         OVER (PARTITION BY account_id
           ORDER BY player_profile_id) - 1 AS index
       FROM player_profile
     ) x
WHERE player_profile.player_profile_id = x.player_profile_id;
ALTER TABLE player_profile ALTER COLUMN index SET NOT NULL;
