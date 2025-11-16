CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    stripe_payment_intent_id VARCHAR(255) UNIQUE NOT NULL,
    furniture_id BIGINT NOT NULL REFERENCES furnitures(id),
    buyer_id BIGINT NOT NULL REFERENCES users(id),
    seller_id BIGINT NOT NULL REFERENCES users(id),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'EUR',
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);