INSERT INTO users (firstname, lastname, email, password, city_id, role, created_at, updated_at)
VALUES (
    'Admin',
    'Système',
    'admin@ampunv.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    41,
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;