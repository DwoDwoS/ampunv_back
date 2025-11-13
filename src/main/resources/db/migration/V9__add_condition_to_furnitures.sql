ALTER TABLE furnitures
ADD COLUMN IF NOT EXISTS condition VARCHAR(50) NOT NULL DEFAULT 'Bon état';

UPDATE furnitures
SET condition = 'Bon état'
WHERE condition IS NULL OR condition = '';

COMMENT ON COLUMN furnitures.condition IS 'État du meuble (Neuf, Très bon état, Bon état, État correct, À rénover)';