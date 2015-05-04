CREATE TABLE device_registration (
  registration_id     TEXT PRIMARY KEY,
  account_id          INTEGER NOT NULL REFERENCES account(account_id),
  device_type         TEXT NOT NULL
);
