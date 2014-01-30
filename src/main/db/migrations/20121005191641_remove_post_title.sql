ALTER TABLE post RENAME COLUMN body to content;
UPDATE post SET content = title || ': ' || content;
ALTER TABLE post DROP COLUMN title;