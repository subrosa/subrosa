ALTER TABLE address ADD COLUMN user_provided TEXT;
UPDATE address SET user_provided = full_address;

ALTER TABLE address ADD COLUMN street_address TEXT;
ALTER TABLE address ADD COLUMN street_address_2 TEXT;
ALTER TABLE address ADD COLUMN city TEXT;
ALTER TABLE address ADD COLUMN state TEXT;
ALTER TABLE address ADD COLUMN country TEXT;
ALTER TABLE address ADD COLUMN postal_code TEXT;

