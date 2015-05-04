ALTER TABLE account ADD COLUMN last_logged_in TIMESTAMP;
CREATE INDEX account_last_logged_in_idx ON account(last_logged_in);

