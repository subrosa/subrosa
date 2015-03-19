INSERT INTO restriction (game_id, type, value)
    SELECT game_id, 'AGE', min_age FROM game;

ALTER TABLE game DROP COLUMN min_age;

