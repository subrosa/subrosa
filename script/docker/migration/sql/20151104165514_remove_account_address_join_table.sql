ALTER TABLE address ADD COLUMN account_id INTEGER;
ALTER TABLE address ADD COLUMN index INTEGER;
CREATE UNIQUE INDEX ON address(account_id, index);

UPDATE address SET account_id = aa.account_id, index = aa.index
FROM account_address aa
WHERE address.address_id = aa.address_id;

DROP TABLE account_address;
