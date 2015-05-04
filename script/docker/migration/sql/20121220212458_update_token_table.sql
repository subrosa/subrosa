ALTER TABLE token RENAME account_token_id TO token_id;
ALTER SEQUENCE account_token_account_token_id_seq RENAME TO token_token_id_seq;
CREATE UNIQUE INDEX token_token_idx ON token(token);