CREATE TABLE images (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    name VARCHAR(255) NOT NULL,
    alt_text VARCHAR(255),
    size_bytes BIGINT,
    mime_type VARCHAR(100),
    width_px INTEGER,
    height_px INTEGER,
    furniture_id BIGINT NOT NULL REFERENCES furnitures(id) ON DELETE CASCADE,
    display_order INTEGER DEFAULT 0 NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT check_positive_size CHECK (size_bytes IS NULL OR size_bytes > 0),
    CONSTRAINT check_positive_dimensions CHECK (
        (width_px IS NULL OR width_px > 0) AND
        (height_px IS NULL OR height_px > 0)
    ),
    CONSTRAINT check_valid_display_order CHECK (display_order >= 0)
);

CREATE INDEX idx_images_furniture ON images(furniture_id);
CREATE INDEX idx_images_is_primary ON images(furniture_id, is_primary) WHERE is_primary = TRUE;
CREATE INDEX idx_images_display_order ON images(furniture_id, display_order);
CREATE INDEX idx_images_created_at ON images(created_at DESC);
CREATE UNIQUE INDEX idx_images_one_primary_per_furniture
ON images(furniture_id)
WHERE is_primary = TRUE;

CREATE TRIGGER trigger_images_updated_at
    BEFORE UPDATE ON images
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION set_first_image_as_primary()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM images WHERE furniture_id = NEW.furniture_id AND id != NEW.id
    ) THEN
        NEW.is_primary = TRUE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_first_image_primary
    BEFORE INSERT ON images
    FOR EACH ROW
    EXECUTE FUNCTION set_first_image_as_primary();

COMMENT ON TABLE images IS 'Table des images/photos des meubles';
COMMENT ON COLUMN images.id IS 'Identifiant unique de l''image';
COMMENT ON COLUMN images.url IS 'URL complète de l''image (stockage externe)';
COMMENT ON COLUMN images.name IS 'Nom du fichier original';
COMMENT ON COLUMN images.alt_text IS 'Texte alternatif pour l''accessibilité (SEO et handicap visuel)';
COMMENT ON COLUMN images.size_bytes IS 'Taille du fichier en octets';
COMMENT ON COLUMN images.mime_type IS 'Type MIME de l''image (image/jpeg, image/png, image/webp)';
COMMENT ON COLUMN images.width_px IS 'Largeur de l''image en pixels';
COMMENT ON COLUMN images.height_px IS 'Hauteur de l''image en pixels';
COMMENT ON COLUMN images.furniture_id IS 'Référence vers le meuble associé';
COMMENT ON COLUMN images.display_order IS 'Ordre d''affichage des images (0 = première)';
COMMENT ON COLUMN images.is_primary IS 'Indique si c''est l''image principale du meuble (une seule par meuble)';
COMMENT ON COLUMN images.created_at IS 'Date et heure d''ajout de l''image';
COMMENT ON COLUMN images.updated_at IS 'Date et heure de dernière modification';