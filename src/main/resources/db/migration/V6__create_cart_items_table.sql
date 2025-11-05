CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    furniture_id BIGINT NOT NULL REFERENCES furnitures(id) ON DELETE CASCADE,
    quantity INTEGER DEFAULT 1 NOT NULL,
    price_at_addition DECIMAL(10, 2) NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT check_positive_quantity CHECK (quantity > 0),
    CONSTRAINT check_positive_price CHECK (price_at_addition >= 0),
    CONSTRAINT unique_user_furniture UNIQUE(user_id, furniture_id)
);

CREATE INDEX idx_cart_items_user ON cart_items(user_id);
CREATE INDEX idx_cart_items_furniture ON cart_items(furniture_id);
CREATE INDEX idx_cart_items_user_added ON cart_items(user_id, added_at DESC);

CREATE TRIGGER trigger_cart_items_updated_at
    BEFORE UPDATE ON cart_items
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION check_furniture_available_for_cart()
RETURNS TRIGGER AS $$
DECLARE
    furniture_status furniture_status;
    furniture_seller_id BIGINT;
BEGIN
    SELECT status, seller_id INTO furniture_status, furniture_seller_id
    FROM furnitures
    WHERE id = NEW.furniture_id;

    IF furniture_status != 'APPROVED' THEN
        RAISE EXCEPTION 'Ce meuble n''est pas disponible à l''achat (statut: %)', furniture_status;
    END IF;

    IF furniture_seller_id = NEW.user_id THEN
        RAISE EXCEPTION 'Vous ne pouvez pas acheter votre propre meuble';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_furniture_available
    BEFORE INSERT ON cart_items
    FOR EACH ROW
    EXECUTE FUNCTION check_furniture_available_for_cart();

COMMENT ON TABLE cart_items IS 'Table du panier d''achat des utilisateurs';
COMMENT ON COLUMN cart_items.id IS 'Identifiant unique de l''article dans le panier';
COMMENT ON COLUMN cart_items.user_id IS 'Utilisateur propriétaire du panier';
COMMENT ON COLUMN cart_items.furniture_id IS 'Meuble ajouté au panier';
COMMENT ON COLUMN cart_items.quantity IS 'Quantité (généralement 1 pour des meubles uniques)';
COMMENT ON COLUMN cart_items.price_at_addition IS 'Prix du meuble au moment de l''ajout (pour historique si le prix change)';
COMMENT ON COLUMN cart_items.added_at IS 'Date et heure d''ajout au panier';
COMMENT ON COLUMN cart_items.updated_at IS 'Date et heure de dernière modification';

COMMENT ON CONSTRAINT unique_user_furniture ON cart_items IS 'Un utilisateur ne peut pas ajouter le même meuble deux fois dans son panier';
COMMENT ON CONSTRAINT check_positive_quantity ON cart_items IS 'La quantité doit être supérieure à 0';

CREATE OR REPLACE VIEW cart_with_details AS
SELECT
    ci.id as cart_item_id,
    ci.user_id,
    ci.furniture_id,
    ci.quantity,
    ci.price_at_addition,
    ci.added_at,

    f.title as furniture_title,
    f.description as furniture_description,
    f.price as current_price,
    f.status as furniture_status,
    f.seller_id,

    c.name as city_name,

    i.url as primary_image_url,

    (ci.price_at_addition * ci.quantity) as item_total

FROM cart_items ci
JOIN furnitures f ON ci.furniture_id = f.id
JOIN cities c ON f.city_id = c.id
LEFT JOIN images i ON f.id = i.furniture_id AND i.is_primary = true;

COMMENT ON VIEW cart_with_details IS 'Vue complète du panier avec tous les détails des meubles';