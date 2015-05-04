CREATE TABLE lifecycle (
  lifecycle_id        SERIAL PRIMARY KEY,
  registration_start  TIMESTAMP NOT NULL,
  registration_end    TIMESTAMP NOT NULL,
  game_start          TIMESTAMP NOT NULL,
  game_end            TIMESTAMP NOT NULL
);

CREATE TABLE lifecycle_event (
  lifecycle_id        INTEGER NOT NULL REFERENCES lifecycle(lifecycle_id),
  event_id            SERIAL PRIMARY KEY,
  event_type          TEXT NOT NULL,
  event_class         TEXT NOT NULL
);

CREATE TABLE scheduled_event (
  event_id            INTEGER PRIMARY KEY REFERENCES lifecycle_event(event_id),
  event_date          TIMESTAMP NOT NULL
);

CREATE TABLE triggered_event (
  event_id            INTEGER PRIMARY KEY REFERENCES lifecycle_event(event_id),
  trigger_type        TEXT NOT NULL,
  trigger_event_id    INTEGER NOT NULL REFERENCES lifecycle_event(event_id)
);

CREATE TABLE game_lifecycle (
  game_id             INTEGER PRIMARY KEY REFERENCES game(game_id),
  lifecycle_id        INTEGER NOT NULL REFERENCES lifecycle(lifecycle_id)
);
