ALTER TABLE address ADD COLUMN label VARCHAR(128);
UPDATE address SET label = address_type;
ALTER TABLE address DROP COLUMN address_type;