CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10),
    department VARCHAR(100),
    region VARCHAR(100),
    country VARCHAR(2) DEFAULT 'FR' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT unique_city_postal UNIQUE(name, postal_code)
);

CREATE INDEX idx_cities_name ON cities(name);
CREATE INDEX idx_cities_postal_code ON cities(postal_code);
CREATE INDEX idx_cities_department ON cities(department);
CREATE INDEX idx_cities_region ON cities(region);

COMMENT ON TABLE cities IS 'Table des villes disponibles pour la localisation des utilisateurs et des meubles';
COMMENT ON COLUMN cities.id IS 'Identifiant unique de la ville';
COMMENT ON COLUMN cities.name IS 'Nom de la ville';
COMMENT ON COLUMN cities.postal_code IS 'Code postal de la ville';
COMMENT ON COLUMN cities.department IS 'Département (ex: Paris, Rhône, Nord)';
COMMENT ON COLUMN cities.region IS 'Région administrative (ex: Île-de-France, Auvergne-Rhône-Alpes)';
COMMENT ON COLUMN cities.country IS 'Code pays ISO 3166-1 alpha-2 (par défaut FR pour France)';
COMMENT ON COLUMN cities.created_at IS 'Date d''ajout de la ville dans la base';

INSERT INTO cities (name, postal_code, department, region) VALUES
('Paris', '75000', 'Paris', 'Île-de-France'),
('Boulogne-Billancourt', '92100', 'Hauts-de-Seine', 'Île-de-France'),
('Saint-Denis', '93200', 'Seine-Saint-Denis', 'Île-de-France'),
('Argenteuil', '95100', 'Val-d''Oise', 'Île-de-France'),
('Montreuil', '93100', 'Seine-Saint-Denis', 'Île-de-France'),
('Versailles', '78000', 'Yvelines', 'Île-de-France'),
('Créteil', '94000', 'Val-de-Marne', 'Île-de-France'),
('Lyon', '69000', 'Rhône', 'Auvergne-Rhône-Alpes'),
('Grenoble', '38000', 'Isère', 'Auvergne-Rhône-Alpes'),
('Saint-Étienne', '42000', 'Loire', 'Auvergne-Rhône-Alpes'),
('Villeurbanne', '69100', 'Rhône', 'Auvergne-Rhône-Alpes'),
('Clermont-Ferrand', '63000', 'Puy-de-Dôme', 'Auvergne-Rhône-Alpes'),
('Marseille', '13000', 'Bouches-du-Rhône', 'Provence-Alpes-Côte d''Azur'),
('Nice', '06000', 'Alpes-Maritimes', 'Provence-Alpes-Côte d''Azur'),
('Toulon', '83000', 'Var', 'Provence-Alpes-Côte d''Azur'),
('Aix-en-Provence', '13100', 'Bouches-du-Rhône', 'Provence-Alpes-Côte d''Azur'),
('Avignon', '84000', 'Vaucluse', 'Provence-Alpes-Côte d''Azur'),
('Cannes', '06400', 'Alpes-Maritimes', 'Provence-Alpes-Côte d''Azur'),
('Toulouse', '31000', 'Haute-Garonne', 'Occitanie'),
('Montpellier', '34000', 'Hérault', 'Occitanie'),
('Nîmes', '30000', 'Gard', 'Occitanie'),
('Perpignan', '66000', 'Pyrénées-Orientales', 'Occitanie'),
('Bordeaux', '33000', 'Gironde', 'Nouvelle-Aquitaine'),
('Limoges', '87000', 'Haute-Vienne', 'Nouvelle-Aquitaine'),
('Pau', '64000', 'Pyrénées-Atlantiques', 'Nouvelle-Aquitaine'),
('La Rochelle', '17000', 'Charente-Maritime', 'Nouvelle-Aquitaine'),
('Lille', '59000', 'Nord', 'Hauts-de-France'),
('Amiens', '80000', 'Somme', 'Hauts-de-France'),
('Roubaix', '59100', 'Nord', 'Hauts-de-France'),
('Tourcoing', '59200', 'Nord', 'Hauts-de-France'),
('Calais', '62100', 'Pas-de-Calais', 'Hauts-de-France'),
('Rennes', '35000', 'Ille-et-Vilaine', 'Bretagne'),
('Brest', '29200', 'Finistère', 'Bretagne'),
('Quimper', '29000', 'Finistère', 'Bretagne'),
('Lorient', '56100', 'Morbihan', 'Bretagne'),
('Strasbourg', '67000', 'Bas-Rhin', 'Grand Est'),
('Reims', '51100', 'Marne', 'Grand Est'),
('Metz', '57000', 'Moselle', 'Grand Est'),
('Nancy', '54000', 'Meurthe-et-Moselle', 'Grand Est'),
('Mulhouse', '68100', 'Haut-Rhin', 'Grand Est'),
('Nantes', '44000', 'Loire-Atlantique', 'Pays de la Loire'),
('Angers', '49000', 'Maine-et-Loire', 'Pays de la Loire'),
('Le Mans', '72000', 'Sarthe', 'Pays de la Loire'),
('Saint-Nazaire', '44600', 'Loire-Atlantique', 'Pays de la Loire'),
('Le Havre', '76600', 'Seine-Maritime', 'Normandie'),
('Rouen', '76000', 'Seine-Maritime', 'Normandie'),
('Caen', '14000', 'Calvados', 'Normandie'),
('Dijon', '21000', 'Côte-d''Or', 'Bourgogne-Franche-Comté'),
('Besançon', '25000', 'Doubs', 'Bourgogne-Franche-Comté'),
('Tours', '37000', 'Indre-et-Loire', 'Centre-Val de Loire'),
('Orléans', '45000', 'Loiret', 'Centre-Val de Loire'),
('Ajaccio', '20000', 'Corse-du-Sud', 'Corse'),
('Bastia', '20200', 'Haute-Corse', 'Corse');

ALTER TABLE users
ADD CONSTRAINT fk_users_city 
FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT;