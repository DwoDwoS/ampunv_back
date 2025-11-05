CREATE TYPE furniture_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'SOLD');

COMMENT ON TYPE furniture_status IS 'Statut du meuble: PENDING (en attente de validation), APPROVED (validé et visible), REJECTED (refusé), SOLD (vendu)';

CREATE TABLE furniture_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_furniture_types_name ON furniture_types(name);

COMMENT ON TABLE furniture_types IS 'Types de meubles disponibles (Canapé, Table, Chaise, Armoire, etc.)';
COMMENT ON COLUMN furniture_types.name IS 'Nom du type de meuble';

CREATE TABLE furniture_materials (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_furniture_materials_name ON furniture_materials(name);

COMMENT ON TABLE furniture_materials IS 'Matériaux des meubles (Bois, Métal, Tissu, Cuir, etc.)';
COMMENT ON COLUMN furniture_materials.name IS 'Nom du matériau';

CREATE TABLE colors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    hex_code VARCHAR(7),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_colors_name ON colors(name);

COMMENT ON TABLE colors IS 'Couleurs disponibles pour les meubles';
COMMENT ON COLUMN colors.name IS 'Nom de la couleur';
COMMENT ON COLUMN colors.hex_code IS 'Code hexadécimal de la couleur (optionnel)';

INSERT INTO furniture_types (name, description) VALUES
('Canapé', 'Canapés, sofas et banquettes'),
('Fauteuil', 'Fauteuils et chaises longues'),
('Chaise', 'Chaises de salle à manger, de bureau, etc.'),
('Table', 'Tables de salle à manger, basses, de chevet'),
('Bureau', 'Bureaux et tables de travail'),
('Armoire', 'Armoires et penderies'),
('Commode', 'Commodes et buffets'),
('Étagère', 'Étagères et bibliothèques'),
('Lit', 'Lits simples, doubles, superposés'),
('Meuble TV', 'Meubles TV et supports audiovisuels'),
('Table de chevet', 'Tables de chevet et chevets'),
('Console', 'Consoles d''entrée'),
('Miroir', 'Miroirs décoratifs et fonctionnels'),
('Tabouret', 'Tabourets de bar, de cuisine'),
('Banc', 'Bancs d''intérieur et d''extérieur'),
('Meuble de rangement', 'Meubles de rangement divers'),
('Autre', 'Autres types de meubles');

INSERT INTO furniture_materials (name, description) VALUES
('Bois massif', 'Bois naturel non traité'),
('Bois composite', 'Aggloméré, MDF, contreplaqué'),
('Métal', 'Acier, fer, aluminium'),
('Tissu', 'Textile, toile'),
('Cuir', 'Cuir véritable'),
('Simili-cuir', 'Cuir synthétique'),
('Verre', 'Verre trempé ou non'),
('Plastique', 'Matières plastiques diverses'),
('Rotin', 'Rotin naturel ou synthétique'),
('Marbre', 'Marbre naturel'),
('Velours', 'Tissu velours'),
('Lin', 'Tissu lin'),
('Autre', 'Autre matériau');

INSERT INTO colors (name, hex_code) VALUES
('Blanc', '#FFFFFF'),
('Noir', '#000000'),
('Gris', '#808080'),
('Beige', '#F5F5DC'),
('Marron', '#8B4513'),
('Bois naturel', '#DEB887'),
('Bleu', '#0000FF'),
('Vert', '#008000'),
('Rouge', '#FF0000'),
('Jaune', '#FFFF00'),
('Orange', '#FFA500'),
('Rose', '#FFC0CB'),
('Violet', '#800080'),
('Doré', '#FFD700'),
('Argenté', '#C0C0C0'),
('Multicolore', NULL),
('Autre', NULL);

CREATE TABLE furnitures (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    type_id INTEGER NOT NULL REFERENCES furniture_types(id) ON DELETE RESTRICT,
    material_id INTEGER REFERENCES furniture_materials(id) ON DELETE SET NULL,
    color_id INTEGER REFERENCES colors(id) ON DELETE SET NULL,
    length_cm DECIMAL(8, 2) CHECK (length_cm > 0),
    width_cm DECIMAL(8, 2) CHECK (width_cm > 0),
    height_cm DECIMAL(8, 2) CHECK (height_cm > 0),
    weight_kg DECIMAL(8, 2) CHECK (weight_kg > 0),
    city_id INTEGER NOT NULL REFERENCES cities(id) ON DELETE RESTRICT,
    seller_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status furniture_status DEFAULT 'PENDING' NOT NULL,
    rejection_reason TEXT,
    validated_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    validated_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_furnitures_seller ON furnitures(seller_id);
CREATE INDEX idx_furnitures_city ON furnitures(city_id);
CREATE INDEX idx_furnitures_status ON furnitures(status);
CREATE INDEX idx_furnitures_type ON furnitures(type_id);
CREATE INDEX idx_furnitures_material ON furnitures(material_id);
CREATE INDEX idx_furnitures_color ON furnitures(color_id);
CREATE INDEX idx_furnitures_price ON furnitures(price);
CREATE INDEX idx_furnitures_created_at ON furnitures(created_at DESC);
CREATE INDEX idx_furnitures_status_city ON furnitures(status, city_id);
CREATE INDEX idx_furnitures_status_created ON furnitures(status, created_at DESC);

COMMENT ON TABLE furnitures IS 'Table des meubles proposés à la vente par les utilisateurs';
COMMENT ON COLUMN furnitures.id IS 'Identifiant unique du meuble';
COMMENT ON COLUMN furnitures.title IS 'Titre/nom du meuble';
COMMENT ON COLUMN furnitures.description IS 'Description détaillée du meuble';
COMMENT ON COLUMN furnitures.price IS 'Prix de vente en euros';
COMMENT ON COLUMN furnitures.type_id IS 'Type de meuble (Canapé, Table, etc.)';
COMMENT ON COLUMN furnitures.material_id IS 'Matériau principal du meuble';
COMMENT ON COLUMN furnitures.color_id IS 'Couleur principale du meuble';
COMMENT ON COLUMN furnitures.length_cm IS 'Longueur du meuble en centimètres';
COMMENT ON COLUMN furnitures.width_cm IS 'Largeur du meuble en centimètres';
COMMENT ON COLUMN furnitures.height_cm IS 'Hauteur du meuble en centimètres';
COMMENT ON COLUMN furnitures.weight_kg IS 'Poids du meuble en kilogrammes';
COMMENT ON COLUMN furnitures.city_id IS 'Ville où se trouve le meuble';
COMMENT ON COLUMN furnitures.seller_id IS 'Vendeur du meuble';
COMMENT ON COLUMN furnitures.status IS 'Statut: PENDING (attente), APPROVED (validé), REJECTED (refusé), SOLD (vendu)';
COMMENT ON COLUMN furnitures.rejection_reason IS 'Raison du refus (si status = REJECTED)';
COMMENT ON COLUMN furnitures.validated_by IS 'Administrateur ayant validé/rejeté le meuble';
COMMENT ON COLUMN furnitures.validated_at IS 'Date et heure de validation/rejet';
COMMENT ON COLUMN furnitures.created_at IS 'Date et heure de création de l''offre';
COMMENT ON COLUMN furnitures.updated_at IS 'Date et heure de dernière modification';

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_furnitures_updated_at
    BEFORE UPDATE ON furnitures
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMENT ON FUNCTION update_updated_at_column() IS 'Fonction trigger pour mettre à jour automatiquement la colonne updated_at';