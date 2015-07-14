ALTER TABLE account ADD COLUMN name TEXT;
UPDATE account SET name =
	CASE WHEN first_name IS NOT NULL AND last_name IS NOT NULL
		THEN first_name || ' ' || last_name
	WHEN first_name IS NOT NULL
		THEN first_name
	WHEN last_name IS NOT NULL
		THEN last_name
	ELSE NULL
	END
;
ALTER TABLE account DROP COLUMN first_name;
ALTER TABLE account DROP COLUMN last_name;
