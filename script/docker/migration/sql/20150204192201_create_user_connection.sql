CREATE TABLE user_connection (
  user_connection_id SERIAL PRIMARY KEY,
  account_id INTEGER NOT NULL REFERENCES account(account_id),
  provider_id VARCHAR(255) NOT NULL,
  provider_user_id VARCHAR(255) NOT NULL,
  access_token VARCHAR(255) NOT NULL,
  secret VARCHAR(255),
  refresh_token VARCHAR(255)
);

