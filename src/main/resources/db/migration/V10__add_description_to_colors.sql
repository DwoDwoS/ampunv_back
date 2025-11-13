ALTER TABLE colors
ADD COLUMN IF NOT EXISTS description TEXT;

COMMENT ON COLUMN colors.description IS 'Description de la couleur (optionnel)';