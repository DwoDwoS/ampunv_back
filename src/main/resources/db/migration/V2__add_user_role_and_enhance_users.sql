CREATE TYPE user_role AS ENUM ('CONSUMER', 'SELLER', 'ADMIN');

ALTER TABLE users
ADD COLUMN role user_role DEFAULT 'CONSUMER' NOT NULL;

ALTER TABLE users
ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_city ON users(city_id);
CREATE INDEX idx_users_role ON users(role);

ALTER TABLE users
ADD CONSTRAINT check_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

COMMENT ON TABLE users IS 'Table des utilisateurs de l''application';
COMMENT ON COLUMN users.id IS 'Identifiant unique de l''utilisateur';
COMMENT ON COLUMN users.firstname IS 'Prénom de l''utilisateur';
COMMENT ON COLUMN users.lastname IS 'Nom de famille de l''utilisateur';
COMMENT ON COLUMN users.email IS 'Adresse email unique de l''utilisateur';
COMMENT ON COLUMN users.password IS 'Mot de passe hashé de l''utilisateur';
COMMENT ON COLUMN users.city_id IS 'Référence vers la ville de résidence de l''utilisateur';
COMMENT ON COLUMN users.role IS 'Rôle de l''utilisateur: CONSUMER (consultation/achat), SELLER (peut vendre + acheter), ADMIN (administration complète)';
COMMENT ON COLUMN users.created_at IS 'Date et heure de création du compte utilisateur';
COMMENT ON COLUMN users.updated_at IS 'Date et heure de dernière modification du compte utilisateur';