ALTER TABLE enrollment_field ADD COLUMN required BOOLEAN NOT NULL DEFAULT TRUE;
UPDATE enrollment_field SET required = 'f' WHERE name = 'Last Wish';
UPDATE enrollment_field SET required = 'f' WHERE name = 'Action Photo';
UPDATE enrollment_field SET required = 't' WHERE name = 'Photo ID';
