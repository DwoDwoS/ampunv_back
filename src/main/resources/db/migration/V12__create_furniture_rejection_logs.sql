CREATE TABLE furniture_rejection_logs (
    id BIGSERIAL PRIMARY KEY,
    furniture_id BIGINT NOT NULL,
    furniture_title VARCHAR(255),
    seller_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    rejected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);