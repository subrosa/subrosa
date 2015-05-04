CREATE TABLE target (
  target_id         SERIAL PRIMARY KEY,
  player_id         INTEGER NOT NULL REFERENCES player(player_id),
  target_type       TEXT NOT NULL,
  target_team_id    INTEGER NOT NULL REFERENCES team(team_id)
);

CREATE TABLE target_physical (
  target_id         INTEGER PRIMARY KEY REFERENCES target(target_id),
  name              TEXT NOT NULL,
  description       TEXT NOT NULL,
  location_id       INTEGER NOT NULL REFERENCES location(location_id)
);

CREATE TABLE target_player (
  target_id         INTEGER PRIMARY KEY REFERENCES target(target_id),
  player_id         INTEGER NOT NULL REFERENCES player(player_id)
);

CREATE TABLE target_team (
  target_id         INTEGER PRIMARY KEY REFERENCES target(target_id),
  team_id           INTEGER NOT NULL REFERENCES team(team_id)
);

INSERT INTO target
    SELECT NEXTVAL('target_target_id_seq'), p.player_id, 'PLAYER', target_id
    FROM assignment a
      INNER JOIN team tm ON (a.assassin_id = tm.team_id)
      INNER JOIN player p ON (tm.team_id = p.team_id)
;

INSERT INTO target_player
    SELECT target_id, p.player_id
    FROM target t
      INNER JOIN team tm ON (t.target_team_id = tm.team_id)
      INNER JOIN player p ON (tm.team_id = p.team_id)
;

ALTER TABLE target DROP COLUMN target_team_id;

DROP TABLE assignment;
