ALTER TABLE users ADD COLUMN is_original_admin BOOLEAN DEFAULT FALSE;
UPDATE users
SET is_original_admin = TRUE
WHERE id = '1';