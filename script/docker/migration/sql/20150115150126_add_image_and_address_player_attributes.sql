ALTER TABLE player_attribute ADD COLUMN attribute_type VARCHAR(32) NOT NULL DEFAULT 'TEXT';
ALTER TABLE player_attribute ADD COLUMN value_ref_id INTEGER;

