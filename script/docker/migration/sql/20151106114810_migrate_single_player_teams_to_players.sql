UPDATE player SET team_id = NULL WHERE team_id NOT IN (
    SELECT team_id FROM player GROUP BY team_id HAVING COUNT(1) > 1
);
DELETE FROM team WHERE team_id NOT IN (
    SELECT COALESCE(team_id, 0) FROM player
);
